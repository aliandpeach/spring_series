package com.yk.base.web;

import com.yk.base.filter.SecurityFilter;
import com.yk.base.servlet.CommonServlet;
import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

/**
 * 该类由springboot提供
 * <p>
 * 用于提供web初始化功能
 *
 * embedded tomcat中经过测试，该类优先于 *RegistrationBean注册的Filter
 */
@Configuration
public class BaseWebInitializer implements ServletContextInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        /**
         * 加载log4j2
         */
        servletContext.setInitParameter("log4jConfiguration", "classpath:log4j2.xml");
        servletContext.addListener(new Log4jServletContextListener());

        ServletRegistration.Dynamic servlet = servletContext.addServlet("commonServlet", new CommonServlet());
        servlet.addMapping("/common");

        FilterRegistration.Dynamic filter = servletContext.addFilter("securityFilter", new SecurityFilter());
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
    }
}
