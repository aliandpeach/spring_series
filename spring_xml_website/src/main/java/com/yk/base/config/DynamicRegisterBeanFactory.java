package com.yk.base.config;

import com.yk.demo.model.Moto;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 动态注册一个Bean 到容器中
 */
public class DynamicRegisterBeanFactory implements BeanFactoryPostProcessor
{
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Moto.class);
        // 目标bean注入依赖(已经存在的bean)
        builder.addPropertyReference("sqlSessionTemplate", "sqlSessionTemplate");
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("moto6", builder.getRawBeanDefinition());

        beanFactory.registerSingleton("moto7", new Moto());
    }
}