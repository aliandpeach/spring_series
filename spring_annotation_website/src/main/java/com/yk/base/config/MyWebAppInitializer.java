package com.yk.base.config;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 利用该类可以实现替代web.xml  底层接口是 WebApplicationInitializer
 *
 * 所以BaseWebApplicationInitializer 也可以，
 * 只是需要自己注册Spring的 AnnotationConfigWebApplicationContext ContextLoaderListener DispatcherServlet
 */
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{
    
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class<?>[]{RootConfig.class};
    }
    
    protected Class<?>[] getServletConfigClasses()
    {
        return new Class<?>[]{SpringMvcConfig.class};
    }
    
    
    /**
     * 官方文档需要把url-pattern的属性值配置成*.do
     * 配置成把url-pattern配置为 / 仅仅适用于restful风格的开发
     * 配置成把url-pattern配置为 /* 会导致所有的请求都得不到响应
     * 配置成把url-pattern配置为 * 会导致Tomcat服务器运行不起来
     *
     * 这里之所以可以配置为 /* 是因为 SpringMvcConfig 中配置了 DefaultServletHandlerConfigurer, 这样spring的拦截就又交给tomcat了
     *
     * SpringBoot中配置为spring.mvc.servlet.path = /* ; DispatcherServletAutoConfiguration 自动装配，不配置也行，有默认值 /*
     */
    protected String[] getServletMappings()
    {
        return new String[]{"/*"};
    }
    
    /**
     * 需要增加一些自定义的Listener或者Servlet或者Filter 或者Cookie的参数 因此需要覆盖onStartup方法
     * 注意调用super.onStartUp否则就需要自己初始化ContextLoaderListener DispatcherServlet
     *
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        super.onStartup(servletContext);
        
        servletContext.setInitParameter("log4jConfiguration", "classpath:log4j2.xml");
        servletContext.addListener(new Log4jServletContextListener());
        
        /**
         * 增加一个拦截器
         */
        FilterRegistration.Dynamic filter = servletContext.addFilter("myFilter1", new Filter()
        {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException
            {
            
            }
            
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
                    IOException, ServletException
            {
                System.out.println();
                String uri = ((HttpServletRequest)request).getRequestURI();
                chain.doFilter(request, response);
            }
            
            @Override
            public void destroy()
            {
            
            }
        });
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
        
        /**
         * <session-config>
         *         <cookie-config>
         *             <secure>true</secure>
         *             <http-only>true</http-only>
         *         </cookie-config>
         *         <tracking-mode>COOKIE</tracking-mode>
         *         <session-timeout>5</session-timeout>
         *     </session-config>
         */
        servletContext.getSessionCookieConfig().setHttpOnly(true);
        servletContext.getSessionCookieConfig().setSecure(true);
        
        Set<SessionTrackingMode> sessionTrackingModes = new HashSet<>();
        sessionTrackingModes.add(SessionTrackingMode.COOKIE);
        servletContext.setSessionTrackingModes(sessionTrackingModes);
    }
    
    /**
     * 暂时未找到方法配置 security-constraint
     *
     * @param registration
     */
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        /**
         * <login-config>
         *         <auth-method>CLIENT-CERT</auth-method>
         *         <realm-name>Client Cert Users-only Area</realm-name>
         *     </login-config>
         *     <security-constraint>
         *         <web-resource-collection>
         *             <web-resource-name>secure</web-resource-name>
         *             <url-pattern>/*</url-pattern>
         *         </web-resource-collection>
         *         <user-data-constraint>
         *             <transport-guarantee>CONFIDENTIAL</transport-guarantee>
         *         </user-data-constraint>
         *     </security-constraint>
         */
    }
    
    /**
     * 实现该方法也可以增加拦截器
     *
     * @return Filter[]
     */
    @Override
    protected Filter[] getServletFilters()
    {
        return new Filter[]{
                new Filter()
                {
                    @Override
                    public void init(FilterConfig filterConfig) throws ServletException
                    {
                    }
                    
                    @Override
                    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
                            IOException, ServletException
                    {
                        System.out.println();
                        chain.doFilter(request, response);
                    }
                    
                    @Override
                    public void destroy()
                    {
                    
                    }
                }
        };
    }
}
