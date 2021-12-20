package com.yk.base.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SessionProvider
{
    public static final String SESSION_USER_KEY = "SESSION_USER_KEY";

    @Getter
    @Value("${secret.related.excepts}")
    private String excepts;

    @Getter
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public Authentication getAuthentication(String name)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
