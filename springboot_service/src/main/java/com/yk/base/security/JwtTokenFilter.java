package com.yk.base.security;

import com.yk.base.exception.BaseResponse;
import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
import com.yk.httprequest.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class JwtTokenFilter extends OncePerRequestFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
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
        LOGGER.info("jwt token filter uri {}", uri);
        if (isExclusion(httpServletRequest, Arrays.stream(Optional.ofNullable(jwtTokenProvider.getExcepts()).orElse("").split(";")).collect(Collectors.toSet())))
        {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try
        {
            if (null == token)
            {
//                throw new AccessDeniedException("JWT token verify failed");
//                throw new CustomException("JWT token verify failed", ResponseCode.ACCOUNT_TOKEN_VERIFY_ERROR.code);
                // 这里不用担心,直接往下走就行, 没有SecurityContextHolder.getContext().setAuthentication(auth)这一步,
                // AnonymousAuthenticationFilter会判断getAuthentication是否为空, 如果是空的, 就创建一个匿名的auth， 最后在FilterSecurityInterceptor中抛出AccessDinedException
                // 不过也可以抛出自定义异常CustomException 或者 AccessDeniedException
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            if (null != SecurityContextHolder.getContext().getAuthentication()
                    && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            {
                logger.info("");
            }
            if (jwtTokenProvider.validateToken(token))
            {
                // JWT-token验证完成后, 在这里会更新该登录用户的权限（权限可能是由管理员通过其他接口或者直接在数据库修改的）
                // 保证了token不用重新登录, 用户也可以直接获取权限
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                // 这里应该是为了后续线程执行到Controller前对权限等的校验, 如果删除下面行, 则token虽然校验成功, 但是执行Controller的权限失败
                // 没有setAuthentication的这一步, 后续走到FilterSecurityInterceptor 中, 会抛出AccessDinedException由handler处理, 之后根据是否是匿名用户（未登录）判断是否改处理成AuthenticationException
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("jwt token verify success");
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
            String result = JSONUtil.toJson(new BaseResponse<>(ex.getCode(), ex.getMessage()));
            httpServletResponse.getWriter().write(result);
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
