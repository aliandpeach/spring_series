package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.httprequest.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class JwtTokenFilter extends OncePerRequestFilter
{
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try
        {
            if (null == token)
            {
                throw new CustomException("JWT token verify failed", HttpStatus.FORBIDDEN);
            }
            if (jwtTokenProvider.validateToken(token))
            {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        catch (CustomException ex)
        {
            //this is very important, since it guarantees the user is not authenticated at all
            SecurityContextHolder.clearContext();

            // Filter 中的异常无法被 @RestControllerAdvice捕获, 如果要在这里抛出异常则需要范围更大的捕捉 (实现 ErrorController接口)
            // 因为, 请求进来 会按照 filter -> interceptor -> controllerAdvice -> aspect -> controller的顺序调用
            // 当controller返回异常 也会按照controller -> aspect -> controllerAdvice -> interceptor -> filter来依次抛出
            // Filter发生的404、405、500错误都会到Spring默认的异常处理 (BasicErrorController -error方法上)
            // 从这里抛出自定义异常在spring-security中会报异常 (FilterChainProxy.java:334)
//             throw new CustomException(ex.getMessage(), ex.getHttpStatus());

            // sendError 之后会请求到 BasicErrorController -error方法上
            // sendError 方法会设置对应的响应状态码，同时返回一个 HTML 格式的错误页，设置响应类型为text/html。
            // 其结果直接造成响应被转发到 /error 的 BasicErrorController
            // 所以使用了 getWriter 就不要使用 sendError 了 否则请求放无法获得 application/json格式的数据
            // 1.
//            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
//            return;

            // 2.
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.setContentType("application/json");
            String result = JSONUtil.toJson(new CustomException(ex.getMessage(), ex.getHttpStatus()));
            httpServletResponse.getWriter().write(result == null ? "{\"message\": \"" + ex.getMessage() + "\"}" : result);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
