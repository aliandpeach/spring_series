package com.yk.base.config;

import com.yk.base.handler.BaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * 总结： 1. DispatcherServlet在通常情况下，url-mapping配置为 *.do/*.html 这样就只会拦截这些后缀的请求，静态资源等直接根据目录可以访问到（不要放到WEB-INF目录）
 *
 *       2. 如果配置了 /* DispatcherServlet 就会拦截所有请求，这样静态资源的配置就必须增加  DefaultServletHandlerConfigurer 才能按照目录去访问静态资源
 *
 *          也可通过配置 addResourceHandler 来解决: webapp 目录下的 static目录被映射为resource
 *          registry.addResourceHandler("/resource/**").addResourceLocations("/static/");
 *          但是如此配置经过测试，能找到jsp页面资源，却无法解析: 如下行配置
 *          registry.addResourceHandler("/WEB-INF/view/**").addResourceLocations("/WEB-INF/view/");
 *
 *          结论：Spring接管所有请求后，通过配置DefaultServletHandlerConfigurer和addResourceHandler 组合配置来解决jsp和其它资源访问的问题
 *
 *
 *          Thymeleaf模板引擎不需要上述操作，只需要配置了viewResolver 之后，addResourceHandler指定资源就可以了
 *
 */

/**
 * 根据@EnableWebMv源码的解释：
 *  1. @EnableWebMvc 修饰配置类，不做任何继承和实现 就能使用 WebMvcConfigurationSupport 的配置
 *  2. @EnableWebMvc + WebMvcConfigurer
 *  3. 如果WebMvcConfigurer接口没有更多公开的的高级配置，那么就需要继承WebMvcConfigurationSupport， 注意不要添加 @EnableWebMvc
 */
@Configuration //bean.xml
// 开启注解版 SpringMVC 相当 web.xml 该工程完全省略了 web.xml
// 非springboot工程启用该注解不会像springboot工程那样禁止掉WebMvcAutoConfiguration, mvc工程本身也没有自动配置
// 所以如果mvc工程要使用swagger, 必须在addResourceHandlers 增加路径映射
@EnableWebMvc
@Order(2)
// 这里只扫描RestController.class, Controller.class
@ComponentScan(basePackages = "com.yk.*",
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)},
        useDefaultFilters = false)
public class SpringMvcConfig implements WebMvcConfigurer
{
    private final static String CHARACTER_ENCODING = "UTF-8";

    /**
     * thymeleaf模板引擎参数
     */
    public final static String TEMPLATE_PREFIX = "/WEB-INF/templates/";
    public final static String TEMPLATE_SUFFIX = ".html";
    public final static Boolean TEMPLATE_CACHEABLE = false;
    public final static String TEMPLATE_MODE = "HTML5";
    public final static Integer TEMPLATE_ORDER = 1;

    /**
     * 配置视图解析器
     */
    /*@Bean
    public InternalResourceViewResolver viewResolver()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setContentType("text/html");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }*/

    /*@Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }*/

    /**
     * <mvc:default-servlet-handler/>
     *     Spring mvc 不配置这个的话，由于dispatchServlet完全取代了 default servlet(容器)，就访问不到静态资源了， 这时候要么使用addResourceHandler去映射静态资源
     *     (但是这么做 jps无法被解析)，要么就开启 default-servlet-handler， 可直接访问静态资源 (非WEB-INF目录), 同时直接访问jsp也可以被解析
     *
     * 交由web容器默认的servlet处理
     *
     * 如果dispatcherserlet 的urlmapping 配置了/* 则需要配置这里才能访问jsp
     *
     * 每次请求过来，先经过DefaultServletHttpRequestHandler判断是否是静态文件，如果是静态文件，则进行处理，
     * 不是则放行交由DispatcherServlet控制器处理。
     */
    /*@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }*/

    /**
     * 模板解析器
     *
     * @return
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver()
    {
        SpringResourceTemplateResolver templateResolver1 = new SpringResourceTemplateResolver();
        templateResolver1.setPrefix(TEMPLATE_PREFIX);
        templateResolver1.setSuffix(TEMPLATE_SUFFIX);
        templateResolver1.setCacheable(TEMPLATE_CACHEABLE);
        templateResolver1.setCharacterEncoding(CHARACTER_ENCODING);
        templateResolver1.setTemplateMode(TEMPLATE_MODE);
        templateResolver1.setOrder(TEMPLATE_ORDER);
        return templateResolver1;
    }

    /**
     * 模板引擎
     *
     * @return
     */
    @Bean
    public SpringTemplateEngine springTemplateEngine(SpringResourceTemplateResolver templateResolver)
    {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    /**
     * 视图解析器
     *
     * @return
     */
    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine springTemplateEngine)
    {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(springTemplateEngine);
        viewResolver.setCharacterEncoding(CHARACTER_ENCODING);
        return viewResolver;
    }

    @Bean
    public BaseHandler baseHandler()
    {
        //自定义拦截器交给spring管理
        return new BaseHandler();
    }

    /**
     * 重写WebMvcConfigurer的addInterceptors方法
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        //添加拦截器
        registry.addInterceptor(baseHandler()).addPathPatterns("/**");
    }

    /**
     * 处理静态资源的，例如：图片，js，css等
     *
     * 当你请求http://localhost:port/resource/1.png时，会把/WEB-INF/static/1.png返回。
     * 注意：这里的静态资源是放置在WEB-INF目录下的。
     *
     * <mvc:resources mapping="/resource/**" location="/WEB-INF/static/"/>
     *
     * 要使得该配置生效，必须让 dispatcherservlet 拦截所有的请求, 因为这个资源是交给spring处理的，前提就得让spring-dispatcherservlet去拦截
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**").addResourceLocations("/static/");
        // static在WEB-INF下就这么配置
        registry.addResourceHandler("/other/**").addResourceLocations("/WEB-INF/static/");
        registry.addResourceHandler("/jquery/**").addResourceLocations("/static/");
    }

    /**
     * 此方法可以很方便的实现一个请求到视图的映射，而无需书写controller，例如：
     */
    /*@Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/now").setViewName("now");
    }*/

    @Bean
    public CommonsMultipartResolver commonsMultipartResolver()
    {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(102400000);
        commonsMultipartResolver.setMaxInMemorySize(102400);
        commonsMultipartResolver.setDefaultEncoding(CHARACTER_ENCODING);
        return commonsMultipartResolver;
    }
}
