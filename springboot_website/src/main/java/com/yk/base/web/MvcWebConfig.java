package com.yk.base.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class MvcWebConfig implements WebMvcConfigurer {

    public final static String CHARACTER_ENCODING = "UTF-8";

    /**
     * thymeleaf模板引擎参数
     */
    public final static String TEMPLATE_PREFIX = "classpath:/templates/";
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

    /**
     * 编码配置
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // web工程的WEB-INF为安全目录, 必须指定资源映射, 另外SpringBoot中 dispatchServlet的默认拦截为"/", 也是不能随便访问其他资源路径, 都需要指定映射
        registry.addResourceHandler("/resource/**").addResourceLocations("classpath:/templates/static/");
        registry.addResourceHandler("/video/**").addResourceLocations("/upload/video/");
    }
}
