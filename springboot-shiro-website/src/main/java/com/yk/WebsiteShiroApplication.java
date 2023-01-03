package com.yk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
public class WebsiteShiroApplication extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        SpringApplication.run(WebsiteShiroApplication.class);
    }

    /**
     * 方法覆盖 Tomcat启动war包的配置
     */
    public SpringApplicationBuilder configure(SpringApplicationBuilder builder)
    {
        return builder.sources(WebsiteShiroApplication.class);
    }
}
