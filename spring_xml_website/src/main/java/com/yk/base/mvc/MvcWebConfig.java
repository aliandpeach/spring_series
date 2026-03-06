package com.yk.base.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * 基于 XML 配置的 Spring MVC 项目，并且已经在 XML 文件中通过 <mvc:annotation-driven /> 启用了 Spring MVC,
 * 那么 MvcWebConfig 类可以不使用 @EnableWebMvc，仅通过实现 WebMvcConfigurer 来扩展配置
 *
 * 注意: @EnableWebMvc 仅在纯Java Config的Spring MVC项目中使用 (纯配置的项目无法直接使用<mvc:annotation-driven />)
 *
 * WebMvcConfigurer用于扩展配置, 不启用Spring MVC则不生效
 *
 * ———————annotation-driven—————————
 * <mvc:annotation-driven/>
 * 这个配置会自动注册三个类
 * RequestMappingHandlerMapping
 * RequestMappingHandlerAdapter
 * ExceptionHandlerExceptionResolver
 * 以支持使用注解@Controller的注解方法（如@RequestMapping、@ExceptionHandler）来处理request，并开启一系列默认功能设置，
 * 有了这个配置，就能将所有请求映射到@Controller修饰的handler上，以及对应的@RequestMapping上，仅仅配置这个，可以处理数据接口，但是无法映射静态资源，这时我们再添加一个配置即可
 * <mvc:default-servlet-handler />
 * 这个配置会注入一个org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler类型的handler。
 * 这个handler会将所有请求过滤，如果是注册过的uri就分派到对应的@Controller，如果不是就给到容器默认的Servlet处理，
 * 一般容器都会有默认的请求处理Servlet且名字为default。这两个配置就能将处理所有请求了，包括静态资源的。
 * ———————annotation-driven—————————
 *
 */
@Configuration
public class MvcWebConfig implements WebMvcConfigurer {

    public final static String CHARACTER_ENCODING = "UTF-8";

    /**
     * thymeleaf模板引擎参数
     */
    public final static String TEMPLATE_PREFIX = "/WEB-INF/templates/";
    public final static String TEMPLATE_SUFFIX = ".html";
    public final static Boolean TEMPLATE_CACHEABLE = false;
    public final static String TEMPLATE_MODE = "HTML";
    public final static Integer TEMPLATE_ORDER = 1;

    /**
     * 模板解析器
     *
     * @return
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix(TEMPLATE_PREFIX);
        templateResolver.setSuffix(TEMPLATE_SUFFIX);
        templateResolver.setCacheable(TEMPLATE_CACHEABLE);
        templateResolver.setCharacterEncoding(CHARACTER_ENCODING);
        templateResolver.setTemplateMode(TEMPLATE_MODE);
        templateResolver.setOrder(TEMPLATE_ORDER);
        return templateResolver;
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
}
