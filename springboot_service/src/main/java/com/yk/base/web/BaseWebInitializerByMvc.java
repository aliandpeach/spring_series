package com.yk.base.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * ServletContextInitializer接口由springMVC提供， 本质上是实现了Servlet3.0的接口功能（实现类是SpringServletContainerInitializer）
 * <p>
 * 详情可以依旧参考website_servlet30工程的样例
 * <p>
 * 普通web工程或者SpingMVC工程该类可生效，在Springboot中防止污染官方统一提供了ServletContextInitializer接口来代替
 * 因此该类应该不会生效（这里写出来是做测试的, 测试发现以外部tomcat部署好像可以生效...呵呵哒，我记得测试的内置tomcat是不生效的）
 * 确实是这样的，embedded tomcat不生效哦 (原理肯定是WebAppServiceLoader类有关)
 */
@Configuration
public class BaseWebInitializerByMvc implements WebApplicationInitializer {
    private Logger logger = LoggerFactory.getLogger("base");

    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("BaseWebInitializerByMvc onStartup");
    }
}
