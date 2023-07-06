package com.yk.base.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>
{
    private SessionProvider sessionProvider;

    private AuthenticationManager authenticationManager;

    public SessionFilterConfigurer(SessionProvider sessionProvider, AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public void configure(HttpSecurity http)
    {
        // 自定义密码校验拦截器放在session校验拦截器之前
        PasswordFilter passwordFilter = new PasswordFilter(sessionProvider);
        http.addFilterBefore(passwordFilter, SessionManagementFilter.class);

        SessionAuthenticationStrategy strategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        // 密码校验拦截器放在自定义密码校验拦截器之前
        http.addFilterBefore(new PasswordAuthenticationFilter(authenticationManager, strategy), passwordFilter.getClass());

        SessionFilter sessionFilter = new SessionFilter(sessionProvider);
        http.addFilterBefore(sessionFilter, PasswordAuthenticationFilter.class);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/api/**", config);
        http.addFilterBefore(new CorsFilter(source), PasswordAuthenticationFilter.class);

        // 添加header设置，支持跨域和ajax请求
        http.addFilterAfter(new OncePerRequestFilter()
        {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
            {
                if (request.getMethod().equals("OPTIONS"))
                {
                    response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
                    response.setHeader("Access-Control-Allow-Headers", response.getHeader("Access-Control-Request-Headers"));
                    return;
                }
                filterChain.doFilter(request, response);
            }
        }, CorsFilter.class);
    }
}
