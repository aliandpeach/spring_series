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
 * 当未登录或者session失效访问接口时，自定义的返回结果
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    /**
     * 若sessionFilter中未setAuthentication则会进入该方法( ExceptionTranslationFilter -> exception -> handleSpringSecurityException -> )
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String res = JSONUtil.toJson(new BaseResponse<String>(ResponseCode.AUTHENTICATION_EXCEPTION.code, ResponseCode.AUTHENTICATION_EXCEPTION.message));
        response.getWriter().write(res);
    }
}
