package com.yk.base.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.List;

/**
 * 动态注册一个Bean 到容器中
 */
public class DynamicDefinitionRegistryBeanFactory implements BeanDefinitionRegistryPostProcessor
{
    private Class<?> beanClass;

    private String beanName;

    private List<Object> values;

    public DynamicDefinitionRegistryBeanFactory(Class<?> beanClass, String beanName, List<Object> values)
    {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.values = values;
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException
    {
        //构造bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        //注册bean定义
        registry.registerBeanDefinition(beanName, beanDefinition);

        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        values.forEach(beanDefinitionBuilder::addConstructorArgValue);
        beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException
    {
    }
}