package com.yk.base.security;

import com.yk.base.exception.BaseResponse;
import com.yk.base.exception.ResponseCode;
import com.yk.httprequest.JSONUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 鉴权异常, 未登录状态下无法触发这个Handler
 * 该异常类在 @RestControllerAdvice 全局异常中无法被执行到, 会被全局异常类优先处理 (权限校验应该是在controller层或者Interceptor而不是Filter中)
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/18 19:03:40
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler
{
    /**
     * FilterSecurityInterceptor -> AbstractSecurityInterceptor.beforeInvocation -> accessDecisionManager.decide -> throw AccessDeniedException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String res = JSONUtil.toJson(new BaseResponse<String>(ResponseCode.UNAUTHORIZED_EXCEPTION.code, ResponseCode.UNAUTHORIZED_EXCEPTION.message));
        response.getWriter().write(res);
    }
}
