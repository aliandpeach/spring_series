package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
import com.yk.httprequest.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 使用了spring-session管理保存session在redis中, 因此不需要使用该拦截器了
 */
// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class SessionFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);

    private SessionProvider sessionProvider;

    public SessionFilter(SessionProvider sessionProvider)
    {
        this.sessionProvider = sessionProvider;
    }

    boolean isExclusion(HttpServletRequest request, Set<String> excludesPattern)
    {
        if (null == excludesPattern || excludesPattern.size() == 0)
        {
            return false;
        }
        for (String exclude : excludesPattern)
        {
            if (!this.matches(exclude, request))
            {
                continue;
            }
            return true;
        }
        return false;
    }

    boolean matches(String pattern, HttpServletRequest request)
    {
//        if (null != pattern && null != source)
//        {
//            pattern = pattern.trim();
//            source = source.trim();
//            int start;
//            if (pattern.endsWith("*"))
//            {
//                start = pattern.length() - 1;
//                return source.length() >= start && pattern.substring(0, start).equals(source.substring(0, start));
//            }
//            else if (pattern.startsWith("*"))
//            {
//                start = pattern.length() - 1;
//                return source.length() >= start && source.endsWith(pattern.substring(1));
//            }
//            else if (pattern.contains("*"))
//            {
//                start = pattern.indexOf("*");
//                int end = pattern.lastIndexOf("*");
//                return source.startsWith(pattern.substring(0, start)) && source.endsWith(pattern.substring(end + 1));
//            }
//            else
//            {
//                return pattern.equals(source);
//            }
//        }
//        else
//        {
//            return false;
//        }
        AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(pattern);
        return antPathRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        String uri = httpServletRequest.getRequestURI();
        LOGGER.info("session filter uri {}", uri);
        if (isExclusion(httpServletRequest, Arrays.stream(Optional.ofNullable(sessionProvider.getExcepts()).orElse("").split(";")).collect(Collectors.toSet())))
        {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        HttpSession session = httpServletRequest.getSession(false);
        if (null == session)
        {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        try
        {
            if (session.getAttribute("SPRING_SECURITY_CONTEXT") instanceof SecurityContext)
            {
                SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
                Authentication authentication = securityContext.getAuthentication();
                Authentication authentication2 = SecurityContextHolder.getContext().getAuthentication();
                LOGGER.debug("session filter securityContext {}", authentication != null);
            }
            // Session验证完成后, 在这里会更新该登录用户的权限（权限可能是由管理员通过其他接口或者直接在数据库修改的）
            // 保证了token不用重新登录, 用户也可以直接获取权限
            // Authentication auth = sessionProvider.getAuthentication(user.getName());
            // 这里应该是为了后续线程执行到Controller前对权限等的校验, 如果删除下面行, 则token虽然校验成功, 但是执行Controller的权限失败
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
            httpServletResponse.getWriter().write("{\"code\":\"" + ex.getCode() + "\",\"message\": \"" + ex.getMessage() + "\"}");
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
