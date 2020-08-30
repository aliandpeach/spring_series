package com.yk.base.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * Servlet3.0 提供的接口， 需要配置BaseWebInitializerTest1的全类限定名到 javax.servlet.ServletContainerInitializer 文件中
 * 该文件在 需要打包到 META-INF/services目录中， 详情可以参考website_servlet30工程的样例
 * <p>
 * 普通web工程或者SpingMVC工程该类可生效，在Springboot中防止污染官方统一提供了ServletContextInitializer接口来代替
 * 因此该类应该不会生效（这里写出来是做测试的, 测试发现以外部tomcat部署好像可以生效...呵呵哒，我记得测试的内置tomcat是不生效的）
 */
public class BaseWebInitializerByServlet30 implements ServletContainerInitializer {
    private Logger logger = LoggerFactory.getLogger("base");

    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("BaseWebInitializerByServlet30 onStartup");
    }
}
