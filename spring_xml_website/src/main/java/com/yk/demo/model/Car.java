package com.yk.demo.model;


import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

import java.util.*;

@Data
public class Car implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {

    List<Moto> listMoto = new ArrayList<>();
    List<Integer> listInteger = new ArrayList<>();
    Map<Integer, String> map = new HashMap<>();

    List<Integer> utilTestList = new LinkedList<>();

    public List<Integer> getUtilTestList() {
        return utilTestList;
    }

    public void setUtilTestList(List<Integer> utilTestList) {
        this.utilTestList = utilTestList;
    }

    private String brand;

    private String color;

    private int speed;

    private BeanFactory beanFactory;

    public Car() {
    }

    public Car(String brand, String color, int speed) {
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
     * 利用静态方法初始化 Car Bean ， 注解中无需写factory-bean
     *
     * @return Car
     */
    public static Car newCar(String brand, String color, String speed) {
        return new Car();
    }

    public List<Moto> getListMoto() {
        return listMoto;
    }

    public void setListMoto(List<Moto> listMoto) {
        this.listMoto = listMoto;
    }

    public List<Integer> getListInteger() {
        return listInteger;
    }

    public void setListInteger(List<Integer> listInteger) {
        this.listInteger = listInteger;
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    public void setMap(Map<Integer, String> map) {
        this.map = map;
    }
}
