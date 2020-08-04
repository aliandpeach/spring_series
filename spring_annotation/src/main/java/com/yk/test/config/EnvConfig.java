package com.yk.test.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/env.properties")
public class EnvConfig {

    @Getter
    @Setter
    @Value("${jdbc1.driver}")
    private String driver;

    @Getter
    @Setter
    @Value("${jdbc1.url}")
    private String url;

    @Getter
    @Setter
    @Value("${jdbc1.username}")
    private String username;

    @Getter
    @Setter
    @Value("${jdbc1.password}")
    private String password;

    @Getter
    @Setter
    @Value("${jdbc1.maxActive}")
    private int maxActive;

    @Bean
    public EnvConfig newEnvConfig() {
        return new EnvConfig();
    }
}