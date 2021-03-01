package com.yk.base.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
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
 * <p>
 * 详情可以依旧参考website_servlet30工程的样例
 * <p>
 *
 * 普通web工程或者SpringMVC工程该类可生效，在SpringBoot中防止污染官方统一提供了ServletContextInitializer接口来代替
 * 因此该类应该不会生效（这里写出来是做测试的, 测试结果：外部tomcat部署可以生效， 内置tomcat是不生效的）
 *
 * 也就是WebApplicationInitializer目前只对war启动的项目有效，对jar启动的项目无效。
 */
@Configuration
public class BaseWebInitializerByMvc implements WebApplicationInitializer {
    private Logger logger = LoggerFactory.getLogger("base");

    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("BaseWebInitializerByMvc onStartup");
    }
}
