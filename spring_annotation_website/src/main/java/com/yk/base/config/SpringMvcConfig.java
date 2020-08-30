package com.yk.base.config;

import com.yk.base.handler.BaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 如果使用继承 WebMvcConfigurationSupport 的方式
 * 则 @EnableWebMvc 需要关闭，因为@EnableWebMvc底层就是继承 WebMvcConfigurationSupport
 * <p>
 * 在spring boot中如果定义了自己的java配置文件，并且在文件上使用了@EnableWebMvc 注解，
 * 那么sprig boot 的默认配置就会失效。
 * 如默认的静态文件配置路径："classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"，将失效。
 * 而有效的配置将只有自己写的java配置 。
 * <p>
 * <p>
 * 有时候我们需要自己定制一些项目的设置，可以有以下几种使用方式：
 * 1、@EnableWebMvc + extends WebMvcConfigurerAdapter，在扩展的类中重写父类的方法即可，
 * 这种方式会屏蔽springboot的@EnableAutoConfiguration 中的设置 (WebMvcConfigurerAdapter 5.0以上已经标记为了Deprecated)
 *
 * 2、extends WebMvcConfigurationSupport，在扩展的类中重写父类的方法即可，
 * 这种方式会屏蔽springboot的@EnableAutoConfiguration 中的设置
 *
 * 3、extends WebMvcConfigurer，
 * 在扩展的类中重写父类的方法即可，这种方式依旧使用springboot的@EnableAutoConfiguration中的设置
 * <p>
 * <p>
 * <p>
 * 使用总结：
 * <p>
 * Spring Boot 默认提供Spring MVC 自动配置，不需要使用@EnableWebMvc注解
 * 如果需要配置MVC（拦截器、格式化、视图等） 请使用添加@Configuration并实现WebMvcConfigurer接口.不要添加@EnableWebMvc注解。
 *
 * @EnableWebMvc 只能添加到一个@Configuration配置类上，用于导入Spring Web MVC configuration
 * 最后，如果Spring Boot在classpath里看到有 spring webmvc 也会自动添加@EnableWebMvc。
 */
@Configuration //相当bean.xml
@ComponentScan(basePackages = {"com.yk"}) //开启扫包
@EnableWebMvc //相当开启注解版springmvc 相当web.xml 该工程完全省略了web.xml
@Order(2)
public class SpringMvcConfig implements WebMvcConfigurer {

    /***配置视图解析器*/
    /*
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }*/

    public final static String CHARACTER_ENCODING = "UTF-8";

    /**
     * thymeleaf模板引擎参数
     */
    public final static String TEMPLATE_PREFIX = "/WEB-INF/templates/";
    public final static String TEMPLATE_SUFFIX = ".html";
    public final static Boolean TEMPLATE_CACHEABLE = false;
    public final static String TEMPLATE_MODE = "HTML5";
    public final static Integer TEMPLATE_ORDER = 1;

    /**
     * 模板解析器
     *
     * @return
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
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
    public SpringTemplateEngine springTemplateEngine(SpringResourceTemplateResolver templateResolver) {
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
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine springTemplateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(springTemplateEngine);
        viewResolver.setCharacterEncoding(CHARACTER_ENCODING);
        return viewResolver;
    }

    @Bean
    public BaseHandler baseHandler() {
        //自定义拦截器交给spring管理
        return new BaseHandler();
    }

    /**
     * 重写WebMvcConfigurer的addInterceptors方法
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        registry.addInterceptor(baseHandler()).addPathPatterns("/**");
    }

    @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(102400000);
        commonsMultipartResolver.setMaxInMemorySize(102400);
        commonsMultipartResolver.setDefaultEncoding(CHARACTER_ENCODING);
        return commonsMultipartResolver;
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(15000);
        httpRequestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        /*SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);
        FormHttpMessageConverter fastConverter = new FormHttpMessageConverter();
        WxMappingJackson2HttpMessageConverter wmc=new WxMappingJackson2HttpMessageConverter();
        restTemplate.getMessageConverters().add(fastConverter);
        restTemplate.getMessageConverters().add(wmc);
        return restTemplate;*/
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();
        converterList.add(new MappingJackson2HttpMessageConverter());
        converterList.add(new FormHttpMessageConverter());
        converterList.add(new MappingJackson2XmlHttpMessageConverter());
        converterList.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(converterList);
        return restTemplate;
    }
}
