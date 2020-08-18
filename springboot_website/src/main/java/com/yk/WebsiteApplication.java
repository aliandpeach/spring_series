package com.yk;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
// 注解类的说明是为了注册@WebListener@WebFilter@WebServlet 但同时也说明了内置tomcat才有用 本工程采用外置tomcat不需要该注解,一样可以生效
@ServletComponentScan
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

    @Bean
    public Servlet dispatcherServlet1() {
        GenericServlet servlet = new GenericServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                res.setContentType("text/plain");
                res.getWriter().append("Hello World");
            }
        };
        return servlet;
    }

    @Bean("errorPage")
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(@Qualifier("errorPage") ErrorPageFilter errorPageFilter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(errorPageFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}
