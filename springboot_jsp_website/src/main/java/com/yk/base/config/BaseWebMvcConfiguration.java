package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * 该配置类继承 WebMvcConfigurationSupport 和@EnableWebMvc作用一致
 * (@EnableWebMvc相当于继承WebMvcConfigurationSupport后没有任何方法覆盖)
 * <p>
 * 使用了@EnableWebMvc 注解， 那么sprig boot 的默认配置就会失效。
 * 如默认的静态文件配置路径："classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"，将失效。
 * <p>
 *    根据@EnableWebMv源码的解释：
 * 1. @EnableWebMvc 修饰配置类，不做任何继承和实现 就能使用 WebMvcConfigurationSupport的配置
 * 2. @EnableWebMvc + WebMvcConfigurer
 * 3. 如果WebMvcConfigurer接口没有更多公开的的高级配置，那么就需要继承 WebMvcConfigurationSupport, 注意不要添加 @EnableWebMvc
 *
 *    WebMvcAutoConfiguration 这个配置类的注解@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
 *    会判断容器是否存在WebMvcConfigurationSupport bean，如果存在，则当前配置类不生效
 *
 *    所以以上3种方式都会使得SpringBoot默认自动配置失效。
 *
 *      如果只实现 WebMvcConfigurer接口，不会覆盖掉 Spring原有的配置, 只会对覆盖的接口进行功能性的增加，
 *      例如覆盖 addResourceHandlers方法，增加 .addResourceHandler("/other/**").addResourceLocations("/png/")
 *      那么Spring默认的和用户增加的映射都可以生效 ( webjars/** classpath:/META-INF/resources/ ...)
 *
 *      继承WebMvcConfigurationSupport 某些配置失效问题
 *      https://blog.csdn.net/weixin_43606226/article/details/105047572
 */
@Configuration
public class BaseWebMvcConfiguration implements WebMvcConfigurer
{
    
    /**
     * springboot中默认的DocumentRoot目录是 src/main/webapp 或者public或者static
     */
    @Bean
    public InternalResourceViewResolver viewResolver()
    {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/views/"); // 不能像thymeleaf一样写成 classpath:/xxxx/ 估计是无法解析打进jar包里的jsp
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        registry.viewResolver(viewResolver());
//        super.configureViewResolvers(registry);
    }

    /**
     * springboot 默认所有请求进入DispatcherServlet中，因此静态资源的路径必须通过定义目录映射才能访问
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/picture/**").addResourceLocations("/png/",
                                                                "classpath:/META-INF/resources/",
                                                                "classpath:/resources/",
                                                                "classpath:/static/",
                                                                "classpath:/public/");
        registry.addResourceHandler("/other/**").addResourceLocations("/png/",
                                                                "classpath:/META-INF/resources/",
                                                                "classpath:/resources/",
                                                                "classpath:/static/",
                                                                "classpath:/public/");
    }
}
