package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.httprequest.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 */
@Component
public class SpringSecurityAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String result = JSONUtil.toJson(new CustomException("认证失败，请重新登录", HttpStatus.FORBIDDEN));
        response.getWriter().write(result == null ? "{\"message\": \"认证失败，请重新登录\"}" : result);
    }
}
