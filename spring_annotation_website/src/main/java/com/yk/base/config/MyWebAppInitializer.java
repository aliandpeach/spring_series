package com.yk.base.config;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.DelegatingFilterProxy;
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
 * 利用该类可以实现替代web.xml  底层接口是 WebApplicationInitializer (怎么启动具体看ServletContainerInitializer的加载配置方式), 因此该类不需要自行new 对象
 *
 * 所以BaseWebApplicationInitializer 也可以，
 * 只是需要自己注册Spring的 AnnotationConfigWebApplicationContext 和 DispatcherServlet
 *
 * 在父级AbstractContextLoaderInitializer 类中 registerContextLoaderListener -> createRootApplicationContext 判断RootConfig类是否为空,
 * 如果为空则默认通过 new ContextLoaderListener() ->  ContextLoader -> ContextLoader.properties 的配置来初始化 XmlWebApplicationContext,
 * 如果不为空最后创建的就是 AnnotationConfigWebApplicationContext
 *
 * XML模式中 ContextLoaderListener 该listener其实就是为了默认创建XmlWebApplicationContext用的
 */
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{

    /**
     * 初始化自定义的 ApplicationContextInitializer接口实现类, 对比web.xml中的 contextInitializerClasses配置
     */
    @Override
    protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers()
    {
        return new ApplicationContextInitializer[]{new ApplicationContextInitializer()
        {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext)
            {

            }
        }};
    }

    /**
     * 初始化自定义的 ApplicationContextInitializer接口实现类, 对比web.xml中的 contextInitializerClasses配置
     */
    @Override
    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers()
    {
        return new ApplicationContextInitializer[]{new ApplicationContextInitializer()
        {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext)
            {

            }
        }};
    }

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
     * SpringBoot中配置为spring.mvc.servlet.path = /* ; ${@link org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration} 自动装配，不配置也行，有默认值 /*
     * DispatcherServletRegistrationBean 作为spring-bean继承自 ServletContextInitializer 会使用 servletContext.addServlet方法添加DispatcherServlet
     * 和使用 ServletRegistrationBean 注册servlet是一个道理
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

        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilter", new DelegatingFilterProxy());
        shiroFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
        super.onStartup(servletContext);
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
                        System.out.println(System.getProperty("user.dir"));
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
