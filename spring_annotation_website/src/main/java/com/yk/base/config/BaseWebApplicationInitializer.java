//package com.yk.base.config;
//
//import org.apache.logging.log4j.web.Log4jServletContextListener;
//import org.springframework.web.WebApplicationInitializer;
//import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import org.springframework.web.servlet.DispatcherServlet;
//
//import javax.servlet.DispatcherType;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRegistration;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.SessionTrackingMode;
//import java.io.IOException;
//import java.util.EnumSet;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Spring MVC
// *
// * SpringServletContainerInitializer 类配置于 META-INF/services/javax.servlet.ServletContainerInitializer中
// *
// * 被 WebappServiceLoader(SPI) 启动， 其@HandlesTypes注释中所注释的接口  WebApplicationInitializer.class
// *
// * 的实现类被默认调用
// *
// */
//public class BaseWebApplicationInitializer implements WebApplicationInitializer {
//
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        servletContext.setInitParameter("log4jConfiguration", "classpath:log4j2.xml");
//        servletContext.addListener(new Log4jServletContextListener());
//        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
//        webApplicationContext.register(MybatisConfig.class, SpringMvcConfig.class, BeanConfig.class);
//        webApplicationContext.setServletContext(servletContext);
//
//        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
//        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
//        servlet.addMapping("*.html");//添加上下文路径地址
////        servlet.setServletSecurity()
//        servlet.setLoadOnStartup(1);//最优先启动
//        servlet.setAsyncSupported(true); //设置允许异步线程
//
//        /**
//         * 增加一个拦截器
//         */
//        FilterRegistration.Dynamic filter = servletContext.addFilter("myFilter1", new Filter()
//        {
//            @Override
//            public void init(FilterConfig filterConfig) throws ServletException
//            {
//
//            }
//
//            @Override
//            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
//                    IOException, ServletException
//            {
//                System.out.println();
//                chain.doFilter(request, response);
//            }
//
//            @Override
//            public void destroy()
//            {
//
//            }
//        });
//        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
//
//        /**
//         * <session-config>
//         *         <cookie-config>
//         *             <secure>true</secure>
//         *             <http-only>true</http-only>
//         *         </cookie-config>
//         *         <tracking-mode>COOKIE</tracking-mode>
//         *         <session-timeout>5</session-timeout>
//         *     </session-config>
//         */
//        servletContext.getSessionCookieConfig().setHttpOnly(true);
//        servletContext.getSessionCookieConfig().setSecure(true);
//
//        Set<SessionTrackingMode> sessionTrackingModes = new HashSet<>();
//        sessionTrackingModes.add(SessionTrackingMode.COOKIE);
//        servletContext.setSessionTrackingModes(sessionTrackingModes);
//    }
//}
