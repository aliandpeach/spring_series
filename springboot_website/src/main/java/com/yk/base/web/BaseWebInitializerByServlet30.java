package com.yk.base.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * ServletContainerInitializer是Servlet3.0 提供的接口，
 * 需要配置 com.yk.base.web.BaseWebInitializerByServlet30 全限定名到 javax.servlet.ServletContainerInitializer 文件中
 * 该文件在 需要打包到 META-INF/services目录中， 详情可以参考website_servlet30工程的样例
 *
 * 普通web工程或者SpringMVC (外置tomcat)工程该类可生效，在SpringBoot中防止污染官方统一提供了 ServletContextInitializer 接口来代替
 * 测试结果是： 外部部署tomcat生效 , embed tomcat是不生效的
 *
 * (embed-tomcat是SpringBoot内置的tomcat 是修改了原生的tomcat代码后的工程)）
 */
public class BaseWebInitializerByServlet30 implements ServletContainerInitializer {
    private Logger logger = LoggerFactory.getLogger("base");

    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("BaseWebInitializerByServlet30 onStartup");
    }
}
