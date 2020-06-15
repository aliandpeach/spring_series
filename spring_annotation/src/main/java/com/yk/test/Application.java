package com.yk.test;

import com.yk.test.config.BeanConfig;
import com.yk.test.config.MybatisConfig;
import com.yk.test.config.SpringContextUtil;
import com.yk.test.example.service.TestBeanServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);
        applicationContext.register(BeanConfig.class);
        TestBeanServiceImpl a = applicationContext.getBean(TestBeanServiceImpl.class);
        TestBeanServiceImpl service = SpringContextUtil.getInstance().getBean(TestBeanServiceImpl.class);
        service.test();

        BeanConfig beanConfig = applicationContext.getBean(BeanConfig.class);
        System.out.println(beanConfig);
        MybatisConfig mybatisConfig = applicationContext.getBean(MybatisConfig.class);
        System.out.println(mybatisConfig);
    }
}
