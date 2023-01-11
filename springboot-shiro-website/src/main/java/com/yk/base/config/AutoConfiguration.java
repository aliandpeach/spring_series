package com.yk.base.config;

import com.yk.base.filter.RequestMetadataFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration
{
    @Bean
    public FilterRegistrationBean<RequestMetadataFilter> requestMetadataFilterRegistration()
    {
        FilterRegistrationBean<RequestMetadataFilter> registrationBean = new FilterRegistrationBean<>();
        RequestMetadataFilter requestMetadataFilter = new RequestMetadataFilter();
        registrationBean.setFilter(requestMetadataFilter);
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }
}
