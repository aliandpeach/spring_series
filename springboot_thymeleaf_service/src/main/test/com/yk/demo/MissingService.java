package com.yk.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * MissingService
 */
@Configuration
public class MissingService
{
    
    @Bean("mapperSrv")
    public Map mmmmmmm()
    {
        System.out.println("mapperSrv");
        return new HashMap();
    }
    
    
    @Bean
    @ConditionalOnClass(MyService.class) // 存在 MyService.class , 就创建servicey
    public MyService serviceOne()
    {
        System.out.println("serviceOne");
        return new MyService();
    }
    
    @Bean
    @ConditionalOnBean(name = "mapperSrv") // 已经存在 name为 mapperSrv 的bean, 就创建 serviceTwo
    public MyService serviceTwo()
    {
        System.out.println("serviceTwo");
        return new MyService();
    }
    
    @Bean
    @ConditionalOnMissingBean // 已经存在 MyService 类型的bean, 就不再创建 serviceThree
    public MyService serviceThree()
    {
        System.out.println("serviceThree");
        return new MyService();
    }
}
