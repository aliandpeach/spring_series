package com.yk.demo.model;


import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

@Data
public class Car implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {

    private String brand;

    private String color;

    private int speed;

    private BeanFactory beanFactory;


    /**
     * spring 将在bean初始化的过程中调用该接口，我们可以通过 this.beanFactory = beanFactory;来保存beanFactory
     * @param beanFactory beanFactory
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
