package com.yk.demo.model;


import lombok.Data;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

@Data
public class Moto implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {

    private Car car4;

    private SqlSessionTemplate sqlSessionTemplate;

    public Car getCar4() {
        return car4;
    }

    public void setCar4(Car car4) {
        this.car4 = car4;
    }

    private String brand;

    private String color;

    private int speed;

    private BeanFactory beanFactory;

    public Moto() {
        System.out.println();
    }

    public Moto(String brand, String color, int speed) {
        this.brand = brand;
        this.color = color;
        this.speed = speed;
    }

    /**
     * spring 将在bean初始化的过程中调用该接口，我们可以通过 this.beanFactory = beanFactory;来保存beanFactory
     *
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

    public void init() throws Exception {
        this.brand = "brand2";
        this.color = "red2";
        this.speed = 1602;
    }

    /**
     * 利用静态方法初始化 Moto Bean ， 注解中无需写factory-bean
     *
     * @return Moto
     */
    public static Moto newMoto(String brand, String color, String speed) {
        return new Moto();
    }
}
