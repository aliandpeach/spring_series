package com.yk.base.config;

import com.yk.base.handler.BaseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * 如果使用继承 WebMvcConfigurationSupport 的方式
 * 则 @EnableWebMvc 需要关闭，因为@EnableWebMvc底层就是继承 WebMvcConfigurationSupport
 *
 * 在spring boot中如果定义了自己的java配置文件，并且在文件上使用了@EnableWebMvc 注解，
 * 那么sprig boot 的默认配置就会失效。
 * 如默认的静态文件配置路径："classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"，将失效。
 * 而有效的配置将只有自己写的java配置 。
 *
 *
 * 有时候我们需要自己定制一些项目的设置，可以有以下几种使用方式：
 * 1、@EnableWebMvc+extends WebMvcConfigurationAdapter，在扩展的类中重写父类的方法即可，
 * 这种方式会屏蔽springboot的@EnableAutoConfiguration中的设置
 * 2、extends WebMvcConfigurationSupport，在扩展的类中重写父类的方法即可，
 * 这种方式会屏蔽springboot的@EnableAutoConfiguration中的设置
 * 3、extends WebMvcConfigurationAdapter/WebMvcConfigurer，
 * 在扩展的类中重写父类的方法即可，这种方式依旧使用springboot的@EnableAutoConfiguration中的设置
 *
 *
 *
 * 使用总结：
 *
 * Spring Boot 默认提供Spring MVC 自动配置，不需要使用@EnableWebMvc注解
 * 如果需要配置MVC（拦截器、格式化、视图等） 请使用添加@Configuration并实现WebMvcConfigurer接口.不要添加@EnableWebMvc注解。
 * @EnableWebMvc 只能添加到一个@Configuration配置类上，用于导入Spring Web MVC configuration
 * 最后，如果Spring Boot在classpath里看到有 spring webmvc 也会自动添加@EnableWebMvc。
 */
@Configuration //相当bean.xml
@ComponentScan(basePackages = {"com.yk"}) //开启扫包
@EnableWebMvc //相当开启注解版springmvc 相当web.xml 该工程完全省略了web.xml
public class SpringMvcConfig implements WebMvcConfigurer {

    /***配置视图解析器*/
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }

    @Bean
    public BaseHandler myHandler() {
        //自定义拦截器交给spring管理
        return new BaseHandler();
    }

    /**
     * 重写WebMvcConfigurer的addInterceptors方法
     */
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        registry.addInterceptor(myHandler()).addPathPatterns("/**");
    }
}
