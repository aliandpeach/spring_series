package com.yk.base.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 该类被 AnnotationConfigWebApplicationContext 使用
 *
 * 相当于 XmlWebApplicationContext 和application.xml的关系
 */
@Configuration
@ComponentScan(basePackages = {"com.yk"}, excludeFilters =
        {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)})

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
public class RootConfig
{

}
