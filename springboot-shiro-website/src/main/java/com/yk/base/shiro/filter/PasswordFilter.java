package com.yk.base.shiro.filter;

import com.yk.base.exception.ResponseCode;
import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.token.PasswordForm;
import com.yk.base.wapper.NewHttpServletRequestWrapper;
import com.yk.httprequest.JSONUtil;
import com.yk.user.form.UserForm;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
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
        PasswordForm passwordForm = new PasswordForm();
        try (InputStream input = request.getInputStream())
        {
            String json = IOUtils.toString(input, StandardCharsets.UTF_8.name());
            UserForm user = JSONUtil.fromJson(json, UserForm.class);
            passwordForm.setUsername(user.getUsername());
//            passwordForm.setPasswd(bcryptPasswordEncoder.encode(user.getPasswd()));
            passwordForm.setPasswd(user.getPasswd());
        }
        catch (IOException e)
        {
            throw new ShiroException(ResponseCode.ACCOUNT_LOGIN_ERROR.message, ResponseCode.ACCOUNT_LOGIN_ERROR.code);
        }

//        User user = userService.queryUserByUsername(passwordForm.getUsername());

        Subject subject = super.getSubject(request, response);
        try
        {
            subject.login(passwordForm);
            return true;
        }
        catch (Exception e)
        {
            if (e instanceof AuthenticationException)
            {
                if (e instanceof DisabledAccountException)
                {
                    // 帐号被锁定
                    if (e instanceof LockedAccountException)
                    {
                        throw new ShiroException(ResponseCode.ACCOUNT_LOCKED_ERROR.message, ResponseCode.ACCOUNT_LOCKED_ERROR.code);
                    }
                    // 帐号被禁用
                    else
                    {
                        throw new ShiroException(ResponseCode.ACCOUNT_DISABLED_ERROR.message, ResponseCode.ACCOUNT_DISABLED_ERROR.code);
                    }
                }
                // 登录失败次数过多
                else if (e instanceof ExcessiveAttemptsException)
                {
                    throw new ShiroException(ResponseCode.ACCOUNT_TOO_MANY_LOGIN_ERROR.message, ResponseCode.ACCOUNT_TOO_MANY_LOGIN_ERROR.code);
                }
                // 未知帐号/没找到帐号
                else if (e instanceof UnknownAccountException)
                {
                    throw new ShiroException(ResponseCode.ACCOUNT_UNKNOWN_ERROR.message, ResponseCode.ACCOUNT_UNKNOWN_ERROR.code);
                }
                else if (e instanceof IncorrectCredentialsException)
                {
                    throw new ShiroException(ResponseCode.ACCOUNT_AUTHENTICATION_ERROR.message, ResponseCode.ACCOUNT_AUTHENTICATION_ERROR.code);
                }
                else
                {
                    if (e.getCause() instanceof ShiroException)
                    {
                        throw (ShiroException) e.getCause();
                    }
                    throw new ShiroException(ResponseCode.ACCOUNT_LOGIN_ERROR.message, ResponseCode.ACCOUNT_LOGIN_ERROR.code);
                }
            }
            else
            {
                throw new ShiroException(ResponseCode.ACCOUNT_LOGIN_ERROR.message, ResponseCode.ACCOUNT_LOGIN_ERROR.code);
            }
        }
    }
}
