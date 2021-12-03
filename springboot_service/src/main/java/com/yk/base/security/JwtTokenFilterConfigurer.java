package com.yk.base.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>
{
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider)
    {
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
    }
}
