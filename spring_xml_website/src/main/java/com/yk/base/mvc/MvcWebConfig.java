package com.yk.base.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * 根据@EnableWebMv源码的解释：
 *  * 1. @EnableWebMvc 修饰配置类，不做任何继承和实现 就能使用WebMvcConfigurationSupport的配置
 *  * 2. @EnableWebMvc + WebMvcConfigurer
 *  * 3. 如果WebMvcConfigurer接口没有更多公开的的高级配置，那么就需要继承WebMvcConfigurationSupport， 注意不要添加 @EnableWebMvc
 *
 *  该工程是xml配置工程应该是不需要该配置类的 (下次测试之后删除)
 */
@Configuration
@EnableWebMvc
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
