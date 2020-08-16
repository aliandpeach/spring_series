package com.yk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WebsiteApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WebsiteApplication.class);
    }

    /**
     * 方法覆盖 Tomcat启动war包的配置
     * @param builder
     * @return
     */
    public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebsiteApplication.class);
    }
}
