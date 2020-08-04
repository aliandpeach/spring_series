package com.yk.test.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.ClassPathResource;

import java.util.Iterator;

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

    @Bean("newBeanConfig")
    public BeanConfig beanConfig(){
        return this;
    }

    /**
     * 在spring boot和spring中,application.properties(以及application.yml)可以放在src/main/resources中,
     * 并由spring环境自动选取.这意味着此文件中的任何属性都将加载到您的环境中,并且可以使用@Value进行注入.
     *
     *
     * 您可以使用PropertySourcesPlaceholderConfigurer来注册更多属性源,如 env.properties, xxx.properties等,以便spring环境添加它们.
     * 当使用@PropertySource时,您将另一个property文件注册到spring环境中,
     * 因此无需使用自定义PropertySourcesPlaceholderConfigurer再次注册它.
     *  .@PropertySource可以更容易地注册不需要像文件系统中的文件那样的特殊加载的属性文件.
     * 只要使用默认位置(application.properties),就不需要注册此类型的自定义bean.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigure() {
        PropertySourcesPlaceholderConfigurer source = new PropertySourcesPlaceholderConfigurer();
        source.setLocation(new ClassPathResource("config/env.properties"));
        return source;
    }
}
