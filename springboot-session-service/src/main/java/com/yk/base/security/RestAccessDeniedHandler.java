package com.yk.base.security;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/18 19:03:40
 */

import com.yk.base.exception.CustomException;
import com.yk.httprequest.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 鉴权
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String result = JSONUtil.toJson(new CustomException("鉴权错误，请重新登录", HttpStatus.FORBIDDEN));
        response.getWriter().write(result == null ? "{\"message\": \"鉴权错误，请重新登录\"}" : result);
    }
}
