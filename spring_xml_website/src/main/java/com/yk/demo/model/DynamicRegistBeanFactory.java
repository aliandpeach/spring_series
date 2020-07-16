package com.yk.demo.model;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 动态注册一个Bean 到容器中
 */
public class DynamicRegistBeanFactory implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Moto.class);
        builder.addPropertyReference("sqlSessionTemplate", "sqlSessionTemplate");
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("moto6", builder.getRawBeanDefinition());

        beanFactory.registerSingleton("moto7", new Moto());
    }
}