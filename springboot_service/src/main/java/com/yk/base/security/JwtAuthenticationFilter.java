package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.base.filter.NewHttpServletRequestWrapper;
import com.yk.db.jpa.dto.UserDataDTO;
import com.yk.httprequest.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 密码校验拦截器 只在登录或者注册或者修改密码的接口中进行拦截
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/18 19:10:29
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    private ThreadLocal<HttpServletRequestWrapper> threadLocal = new ThreadLocal<>();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super.setAuthenticationManager(authenticationManager);
        super.setContinueChainBeforeSuccessfulAuthentication(false);
        super.setFilterProcessesUrl("/api/signin");
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response)
    {
        return new AntPathRequestMatcher("/api/signin").matches(request);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException
    {
        NewHttpServletRequestWrapper httpServletRequestWrapper;
        try
        {
            httpServletRequestWrapper = new NewHttpServletRequestWrapper(request);
        }
        catch (IOException e)
        {
            return super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("", ""));
        }
        threadLocal.set(httpServletRequestWrapper);
        UserDataDTO user = null;
        try (InputStream input = httpServletRequestWrapper.getInputStream())
        {
            String json = IOUtils.toString(input, StandardCharsets.UTF_8.name());
            user = JSONUtil.fromJson(json, UserDataDTO.class);
            LOGGER.info("get request body info {}", json);
        }
        catch (IOException e)
        {
            LOGGER.error("get request body error {}", e.getMessage());
            user = new UserDataDTO();
        }
        String name = Optional.ofNullable(user.getName()).orElse("");
        String pwd = Optional.ofNullable(user.getPasswd()).orElse("");
        return super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(name, pwd));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        chain.doFilter(threadLocal.get(), response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException
    {
        response.setContentType("application/json");
        String result = JSONUtil.toJson(new CustomException("密码校验失败", HttpStatus.FORBIDDEN));
        response.getWriter().write(result == null ? "{\"message\": \"密码校验失败\"}" : result);
    }
}
