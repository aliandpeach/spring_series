package com.yk.base.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Spring MVC
 *
 * SpringServletContainerInitializer 类配置于 META-INF/services/javax.servlet.ServletContainerInitializer中
 *
 * 被 WebappServiceLoader(SPI) 启动， 其@HandlesTypes注释中所注释的接口  WebApplicationInitializer.class
 *
 * 的实现类被默认调用
 *
 */
public class BaseWebApplicationInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(SpringMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        servlet.addMapping("/");//添加上下文路径地址
        servlet.setLoadOnStartup(1);//最优先启动
        servlet.setAsyncSupported(true); //设置允许异步线程
    }
}
