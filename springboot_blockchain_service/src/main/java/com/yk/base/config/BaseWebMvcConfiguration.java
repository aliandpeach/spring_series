package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 该配置类继承 WebMvcConfigurationSupport 和@EnableWebMvc作用一致
 * (@EnableWebMvc相当于继承WebMvcConfigurationSupport后没有任何方法覆盖)
 * <p>
 * <p>
 * 默认的静态文件配置路径："classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"
 *
 * <p>
 * 继承WebMvcConfigurationSupport 某些配置失效问题
 * https://blog.csdn.net/weixin_43606226/article/details/105047572
 */
@Configuration
public class BaseWebMvcConfiguration implements WebMvcConfigurer
{

    private final static String CHARACTER_ENCODING = "UTF-8";

    /**
     * thymeleaf模板引擎参数
     */
    private final static String TEMPLATE_PREFIX = "classpath:/thymeleaf/";
    private final static String TEMPLATE_SUFFIX = ".html";
    private final static Boolean TEMPLATE_CACHEABLE = false;
    private final static String TEMPLATE_MODE = "HTML";
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
        templateResolver1.setOrder(1);
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
        registry.addResourceHandler("/picture/**").addResourceLocations("/picture/");
    }

    /**
     * 页面跳转
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/").setViewName("index");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/goLogin", "/login");
    }

    /**
     * 编码配置
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }
}
