package com.yk.base.shiro.filter;

import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.redis.RedisServiceImpl;
import com.yk.base.shiro.token.PasswordToken;
import com.yk.base.wapper.NewHttpServletRequestWrapper;
import com.yk.httprequest.JSONUtil;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PasswordFilter extends AccessControlFilter
{
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public PasswordFilter(BCryptPasswordEncoder bcryptPasswordEncoder,
                          JwtTokenProvider jwtTokenProvider)
    {
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException
    {
        NewHttpServletRequestWrapper wrapper = new NewHttpServletRequestWrapper((HttpServletRequest) request);
        super.doFilterInternal(wrapper, response, chain);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
    {
        Subject subject = super.getSubject(request, response);
        return null != subject && subject.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
    {
        PasswordToken passwordToken = new PasswordToken();
        try (InputStream input = request.getInputStream())
        {
            String json = IOUtils.toString(input, StandardCharsets.UTF_8.name());
            User user = JSONUtil.fromJson(json, User.class);
            passwordToken.setUsername(user.getUsername());
//            passwordToken.setPasswd(bcryptPasswordEncoder.encode(user.getPasswd()));
            passwordToken.setPasswd(user.getPasswd());
        }
        catch (IOException e)
        {
            throw new ShiroException(400, "sign in error");
        }

//        User user = userService.queryUserByUsername(passwordToken.getUsername());

        Subject subject = super.getSubject(request, response);
        try
        {
            subject.login(passwordToken);
        }
        catch (AuthenticationException e)
        {
            throw new ShiroException(400, "sign in error");
        }
        return true;
    }
}
