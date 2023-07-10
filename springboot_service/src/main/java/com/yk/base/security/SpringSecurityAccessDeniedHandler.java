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
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/02/24 17:58:11
 */
@Component
public class SpringSecurityAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String result = JSONUtil.toJson(new BaseResponse<>(ResponseCode.UNAUTHORIZED_EXCEPTION.code, ResponseCode.UNAUTHORIZED_EXCEPTION.message));
        response.getWriter().write(result);
    }
}
