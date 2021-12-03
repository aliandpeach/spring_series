package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.httprequest.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class JwtTokenFilter extends OncePerRequestFilter
{
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    boolean isExclusion(String requestUri, Set<String> excludesPattern, String contextPath)
    {
        String uri = "/";
        if (null != excludesPattern && null != requestUri)
        {
            if (null != contextPath && requestUri.startsWith(contextPath))
            {
                requestUri = requestUri.substring(contextPath.length());
                if (!requestUri.startsWith(uri))
                {
                    requestUri = "/".concat(requestUri);
                }
            }
            Iterator<String> iterator = excludesPattern.iterator();
            String pattern;
            do
            {
                if (!iterator.hasNext())
                {
                    return false;
                }
                pattern = (String) iterator.next();
            }
            while (!this.matches(pattern, requestUri));
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean matches(String pattern, String source)
    {
        if (null != pattern && null != source)
        {
            pattern = pattern.trim();
            source = source.trim();
            int start;
            if (pattern.endsWith("*"))
            {
                start = pattern.length() - 1;
                return source.length() >= start && pattern.substring(0, start).equals(source.substring(0, start));
            }
            else if (pattern.startsWith("*"))
            {
                start = pattern.length() - 1;
                return source.length() >= start && source.endsWith(pattern.substring(1));
            }
            else if (pattern.contains("*"))
            {
                start = pattern.indexOf("*");
                int end = pattern.lastIndexOf("*");
                return source.startsWith(pattern.substring(0, start)) && source.endsWith(pattern.substring(end + 1));
            }
            else
            {
                return pattern.equals(source);
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        String uri = httpServletRequest.getRequestURI();
        if (isExclusion(uri, Arrays.stream(Optional.ofNullable(jwtTokenProvider.getExcepts()).orElse("").split(";")).collect(Collectors.toSet()), httpServletRequest.getContextPath()))
        {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try
        {
            if (null == token)
            {
                throw new CustomException("JWT token verify failed", HttpStatus.FORBIDDEN);
            }
            if (null != SecurityContextHolder.getContext().getAuthentication()
                    && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            {
                logger.error("");
            }
            if (jwtTokenProvider.validateToken(token))
            {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                // 这里应该是为了后续线程执行到controller前对权限等的校验
                SecurityContextHolder.getContext().setAuthentication(auth);
                UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                logger.error("");
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
