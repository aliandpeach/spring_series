package com.yk.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器,一般用于@RequestBody接受参数时使用
 *
 * 解决前端传递的字符串日期格式, 转换到后端Date后丢失时间问题(验证)
 *
 * 注解配置方式
 * Springboot HttpMessageConvertersAutoConfiguration 中自动装载单个HttpMessageConverter到 HttpMessageConverters
 */
@Configuration
public class ConverterConfig
{

    @Bean
    public DateJacksonConverter dateJacksonConverter()
    {
        return new DateJacksonConverter();
    }

    /**
     * getObject return ObjectMapper
     */
    @Bean
    public Jackson2ObjectMapperFactoryBean jackson2ObjectMapperFactoryBean(DateJacksonConverter dateJacksonConverter)
    {
        Jackson2ObjectMapperFactoryBean jackson2ObjectMapperFactoryBean = new Jackson2ObjectMapperFactoryBean();
        // 自定义时间字符串转Date
        jackson2ObjectMapperFactoryBean.setDeserializers(dateJacksonConverter);
        // 自定义Date转字符串格式
//      jackson2ObjectMapperFactoryBean.setSerializers();
        return jackson2ObjectMapperFactoryBean;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper /*Jackson2ObjectMapperFactoryBean*/ objectMapper)
    {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return mappingJackson2HttpMessageConverter;
    }
}
