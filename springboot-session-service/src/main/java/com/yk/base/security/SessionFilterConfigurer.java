package com.yk.base.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
        // SessionFilter sessionFilter = new SessionFilter(sessionProvider);

        // 在UsernamePasswordAuthenticationFilter 拦截器之前
        // http.addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);

        PasswordFilter passwordFilter = new PasswordFilter(sessionProvider);
        http.addFilterBefore(passwordFilter, SessionManagementFilter.class);

        http.addFilterBefore(new PasswordAuthenticationFilter(authenticationManager), passwordFilter.getClass());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/api/**", config);
        http.addFilterBefore(new CorsFilter(source), PasswordAuthenticationFilter.class);
    }
}
