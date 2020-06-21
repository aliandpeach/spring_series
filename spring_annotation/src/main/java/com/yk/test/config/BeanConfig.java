package com.yk.test.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("com.yk.test")
@PropertySource("classpath:jdbc.properties")
public class BeanConfig {

    @Getter
    @Setter
    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    @Getter
    @Setter
    private String url;

    @Value("${jdbc.username}")
    @Getter
    @Setter
    private String username;

    @Value("${jdbc.password}")
    @Getter
    @Setter
    private String password;

    @Value("${jdbc.maxActive}")
    @Getter
    @Setter
    private int maxActive;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigure() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
