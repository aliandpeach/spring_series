package com.yk.base.shiro.filter;

import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.token.CustomerToken;
import com.yk.base.utils.RequestUtils;
import com.yk.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TokenFilter extends AccessControlFilter
{
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public TokenFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException
    {
        super.doFilterInternal(request, response, chain);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
    {
        String authorization = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        if (null == authorization)
        {
            throw new ShiroException(400, "token not exist");
        }

        String username = null;
        try
        {
            username = jwtTokenProvider.getUsername(authorization);
        }
        catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e)
        {
            if (e instanceof ExpiredJwtException)
            {
                // 如果是正常的jwt, 只是由于过期抛出的异常, 则继续处理, 在login时, 判断是过期后, 根据逻辑判断是否设置refresh token
                Claims claims = ((ExpiredJwtException) e).getClaims();
                username = null != claims ? claims.getSubject() : null;
            }
            // 记录日志
        }
        if (null == username)
        {
            throw new ShiroException(400, "token not support");
        }

        CustomerToken token = new CustomerToken(username, authorization);
        Subject subject = super.getSubject(request, response);
        try
        {
            // 接下来会执行 TokenMatcher
            subject.login(token);
        }
        catch (AuthenticationException e)
        {
            if (e instanceof ExpiredCredentialsException)
            {
                String _key = username.concat(":").concat(authorization).concat(":").concat(RequestUtils.getBaseMetadata().getIp());
                Object tokenObject = jwtTokenProvider.getRedisService().getValue(_key);
                if (null != tokenObject && tokenObject.equals(authorization))
                {
                    User _user = jwtTokenProvider.getUserService().queryUserByUsername(username);
                    String newToken = jwtTokenProvider.createToken((String) token.getPrincipal(), _user.getRoleList());
                    jwtTokenProvider.getRedisService().addValue(
                            _user.getUsername().concat(":").concat(newToken).concat(":").concat(RequestUtils.getBaseMetadata().getIp()),
                            newToken,
                            jwtTokenProvider.getValidityInMilliseconds() / 1000 * 2,
                            TimeUnit.SECONDS);
                    jwtTokenProvider.getRedisService().deleteByKeys(_key);
                    ((HttpServletResponse) response).addHeader("Authorization", newToken);

                    try
                    {
                        // 刷新token后, 需要重新set, 否则如果需要checkRole或者checkPermission时 (@RequiresRoles/@RequiresPermissions), 会在 DelegatingSubject.assertAuthzCheckPossible 抛出 This subject is anonymous 的异常
                        subject.login(new CustomerToken(username, newToken));
                        boolean isAuthenticated = subject.isAuthenticated();
                        logger.debug("isAuthenticated : {}", isAuthenticated);
                    }
                    catch (Exception ee)
                    {
                        logger.error("refresh token re login error", e);
                        throw new ShiroException(400, "token verify error");
                    }
                    return true;
                }
            }
            throw new ShiroException(400, "token verify error");
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
    {
        Subject subject = super.getSubject(request, response);
        return null != subject && subject.isAuthenticated();
    }
}
