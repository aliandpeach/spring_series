package com.yk.base.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 *
 * SpringServletContainerInitializer 实现了Servlet3.0 接口 ServletContainerInitializer
 * WebApplicationInitializer 接口是SpringServletContainerInitializer 类上的注解中的配置类,
 *
 * 自定义实现WebApplicationInitializer 的类可以被Spring自动执行内部的onStartup方法，原因: 参考website_servlet30
 *
 * 详情可以依旧参考website_servlet30工程的样例
 *
 * 外置tomcat的工程, 该类都可生效，在SpringBoot中防止污染官方统一提供了 ServletContextInitializer 接口来代替
 * 测试结果：外部tomcat部署可以生效， 内置tomcat是不生效的
 */
@Configuration
public class BaseWebInitializerByMvc implements WebApplicationInitializer {
    private Logger logger = LoggerFactory.getLogger("base");

    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("BaseWebInitializerByMvc onStartup");
    }
}
