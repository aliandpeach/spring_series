package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig
{
    
    @Bean
    public ClientHttpRequestFactory factory()
    {
        /*SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        return factory;*/
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(15000);
        httpRequestFactory.setReadTimeout(5000);
        return httpRequestFactory;
    }
    
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory)
    {
        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();
        converterList.add(new MappingJackson2HttpMessageConverter());
        converterList.add(new FormHttpMessageConverter());
//        converterList.add(new MappingJackson2XmlHttpMessageConverter());
        converterList.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(converterList);
        return restTemplate;
    }
}
