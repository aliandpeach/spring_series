package com.yk.base.web;

import com.yk.base.filter.BaseFilter;
import com.yk.base.listener.BaseListener;
import com.yk.base.servlet.UploadServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import java.util.Collections;

/**
 * 统一注册的配置类
 */
@Configuration
public class WebXmlConfig
{

    @Bean
    public ServletRegistrationBean<HttpServlet> servletRegistrationBean()
    {
        ServletRegistrationBean<HttpServlet> bean = new ServletRegistrationBean<>();
        bean.setServlet(new UploadServlet());
        bean.addUrlMappings("/upload");
//        bean.addInitParameter();
        return bean;
    }

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean()
    {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new BaseFilter());
        bean.setUrlPatterns(Collections.singletonList("/*"));
//        bean.setInitParameters();
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> servletListenerRegistrationBean()
    {
        ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new BaseListener());
        return bean;
    }
}
