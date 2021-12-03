package com.yk.base.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该拦截器只在 /api/signin 等少数需要校验密码的时候执行 (未完成, 目前校验密码直接写在登录方法中了)
 */
public class PasswordFilter extends OncePerRequestFilter
{
    private JwtTokenProvider jwtTokenProvider;

    public PasswordFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
