package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 该配置类继承 WebMvcConfigurationSupport 和@EnableWebMvc作用一致
 * (@EnableWebMvc相当于继承WebMvcConfigurationSupport后没有任何方法覆盖)
 *
 *
 * 默认的静态文件配置路径："classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"
 *
 * <p>
 * 继承WebMvcConfigurationSupport 某些配置失效问题
 * https://blog.csdn.net/weixin_43606226/article/details/105047572
 * 失效问题: WebMvcConfigurationSupport是webmvc的配置类，如果在springboot项目中，有配置类继承了WebMvcConfigurationSupport，
 * 那么webmvc的自动配置类WebMvcAutoConfiguration就会失效。
 *
 *
 *
 * 1. 在非springboot项目中, 使用 @EnableWebMvc + 实现WebMvcConfigurer接口, 既能使用WebMvcConfigurationSupport的配置, 又能拓展接口配置
 * 2. 在非springboot项目中, 继承WebMvcConfigurationSupport就相当于使用了@EnableWebMvc, 但不要重复添加@EnableWebMvc注释 (这条备注场景没测试过)
 *
 * 1. 在springboot项目中, 不使用@EnableWebMvc注释, 除非做好了不使用自动配置的配置项的准备
 *                       (在springboot工程中没试过使用@EnableWebMvc注释, 但效果应该和第三条备注继承WebMvcConfigurationSupport一样)
 * 2. 在springboot项目中, 只需实现WebMvcConfigurer接口
 * 3. 在springboot项目中, 一般不使用WebMvcConfigurationSupport, 因为继承了WebMvcConfigurationSupport就会失去自动配置(WebMvcAutoConfiguration)中的配置项
 *                       本工程就使用了extends WebMvcConfigurationSupport, 用于测试一些特殊情况
 */
@Configuration
public class BaseWebMvcConfiguration extends WebMvcConfigurationSupport
{
    
    private final static String CHARACTER_ENCODING = "UTF-8";
    
    /**
     * thymeleaf模板引擎参数
     */
    private final static String TEMPLATE_PREFIX = "classpath:/thymeleaf/";
    private final static String TEMPLATE_SUFFIX = ".html";
    private final static Boolean TEMPLATE_CACHEABLE = false;
    private final static String TEMPLATE_MODE = "HTML5";
    private final static Integer TEMPLATE_ORDER = 1;
    
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
        templateResolver1.setCharacterEncoding("UTF-8");
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
    
    /**
     * springboot 默认所有请求进入DispatcherServlet中，因此静态资源的路径必须通过定义目录映射才能访问
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/resource/**").addResourceLocations("classpath:/thymeleaf/static/");
        registry.addResourceHandler("/jquery/**").addResourceLocations("classpath:/thymeleaf/static/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/other/**").addResourceLocations("classpath:/META-INF/resources/");
        // 非classpath下的文件 SpringBoot默认是在src/main/webapp  public  static 目录中, 这三个目录和打出来的jar得在同一级目录
        // 也可以使用factory.setDocumentRoot("test");设置为test目录, 即: test目录和jar同级

        // 这里的 https://192.168.32.152:9027/png/webapp.png 就可以被访问到
        registry.addResourceHandler("/png/**").addResourceLocations("/png/");
    }
    
    /**
     * 页面跳转
     */
    @Override
    protected void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("index").setViewName("index");
    }
    
    /**
     * 拦截器配置
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry)
    {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/goLogin", "/login");
    }
    
    /**
     * 编码配置
     */
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        super.configureMessageConverters(converters);
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }
}
