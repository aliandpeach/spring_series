package com.yk.base.security;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;

@Configuration
public class SecurityConfig
{
    @Bean
    public SpringCacheBasedUserCache springCacheBasedUserCache()
    {
        return new SpringCacheBasedUserCache(new ConcurrentMapCache("spring-security-user-cache"));
    }
}
