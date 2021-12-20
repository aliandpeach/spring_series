package com.yk.base.config;

import com.yk.httprequest.HttpClientUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 *
 * Spring Boot 继承 WebMvcConfigurationSupport 或者使用 @EnableWebMvc 会导致 WebMvcAutoConfiguration自动配置失效, 进而导致：
 * 自动配置的静态资源路径（classpath:/META-INF/resources/，classpath:/resources/，classpath:/static/，classpath:/public/）不生效。
 * 这是因为在 springboot的web自动配置类 WebMvcAutoConfiguration 上有条件注解 @ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
 * WebMvcAutoConfiguration 不生效则其内部自定义的某些目录下的静态资源无法被访问到 (在这个方法中 addResourceHandlers)
 * 如果想要使用自动配置生效，就不要继承 WebMvcConfigurationSupport, 可以实现WebMvcConfigurer接口, 实现的接口方法不会覆盖自动配置。
 *
 * 当 WebMvcAutoConfiguration 不生效时会导致以下几个问题：
 * 1.WebMvcProperties 和 ResourceProperties 失效 因为两个配置类中的属性都在 WebMvcAutoConfiguration 中使用
 * 当WebMvc自动配置失效(WebMvcAutoConfiguration自动化配置)时，会导致无法视图解析器无法解析并返回到对应的视图
 *
 * 2.类路径上的 HttpMessageConverter 失效
 *
 * https://blog.csdn.net/She_lock/article/details/86241685
 * https://blog.csdn.net/zhangpower1993/article/details/89016503
 * https://www.jianshu.com/p/bf9be49eb79b
 *
 * 1. Spring Boot 默认提供Spring MVC 自动配置，不需要使用@EnableWebMvc注解
 * 2. 如果需要配置MVC（拦截器、格式化、视图等） 请使用添加@Configuration并实现WebMvcConfigurer接口.不要添加@EnableWebMvc注解。
 * 3. 注解@EnableWebMvc 只能添加到一个@Configuration配置类上，用于导入Spring Web MVC configuration
 *
 * use @EnableWebMvc or directly extending WebMvcConfigurationSupport will switch off the WebMvcAutoConfiguration
 * 使用 @EnableWebMvc或者继承 WebMvcConfigurationSupport 会关闭自动配置类 WebMvcAutoConfiguration
 */
@Configuration
public class BaseWebMvcConfiguration implements WebMvcConfigurer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseWebMvcConfiguration.class);

    @Bean
    public CloseableHttpClient httpClient() throws GeneralSecurityException, IOException
    {
        return HttpClientUtil.getClient(new HttpClientUtil.Config());
    }

    @Bean
    public ClientHttpRequestFactory factory(CloseableHttpClient httpClient)
    {
        HttpComponentsClientHttpRequestFactory httpRequestFactory;
        try
        {
            // 底层使用 http-client组件代替默认的HttpUrlConnection
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            httpRequestFactory.setConnectTimeout(15000);
            httpRequestFactory.setReadTimeout(5000);
            return httpRequestFactory;
        }
        catch (Exception e)
        {
            LOGGER.error("build HttpComponentsClientHttpRequestFactory error", e);
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectTimeout(15000);
            httpRequestFactory.setReadTimeout(5000);
            return httpRequestFactory;
        }
    }

    /**
     * RestTemplateBuilder由RestTemplateAutoConfiguration 自动组装
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory, RestTemplateBuilder builder)
    {
        // requestFactory方法内部重新new了一个 RestTemplateBuilder对象
        return builder.requestFactory(() -> factory).build();
    }
}
