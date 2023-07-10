package com.yk.base.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>
{
    private JwtTokenProvider jwtTokenProvider;

    private AuthenticationManager authenticationManager;

    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http)
    {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);

        // 在UsernamePasswordAuthenticationFilter 拦截器之前
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        PasswordFilter passwordFilter = new PasswordFilter(jwtTokenProvider);
        http.addFilterBefore(passwordFilter, JwtTokenFilter.class);

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider), passwordFilter.getClass());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/api/**", config);
        http.addFilterBefore(new CorsFilter(source), JwtAuthenticationFilter.class);
    }
}
