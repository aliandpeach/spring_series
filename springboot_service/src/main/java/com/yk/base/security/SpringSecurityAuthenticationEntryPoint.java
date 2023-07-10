package com.yk.base.security;

import com.yk.base.exception.BaseResponse;
import com.yk.base.exception.ResponseCode;
import com.yk.httprequest.JSONUtil;
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
    /**
     * spring-security 在拦截器中抛出的异常统一由ExceptionTranslationFilter进行处理, 如果请求未携带token, 会抛出AccessDeniedException，
     * 但是未携带cookie，会被判定为匿名用户（未登录用户）, 会改为 AuthenticationException进行处理, 用户自行抛出AccessDeniedException也如此处理
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String result = JSONUtil.toJson(new BaseResponse<>(ResponseCode.AUTHENTICATION_EXCEPTION.code, ResponseCode.AUTHENTICATION_EXCEPTION.message));
        response.getWriter().write(result);
    }
}
