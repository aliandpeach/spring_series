package com.yk.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MissingService
 */
@Configuration
public class MissingService
{
    @Bean("servicex")
    @ConditionalOnClass(MyService.class) // 存在 MyService.class , 就创建servicey
    public MyService servicex()
    {
        System.out.println("servicex");
        return new MyService();
    }
    
    @Bean
    @ConditionalOnBean(name = "servicex") // 已经存在 name为servicex 的bean, 就创建servicey
    public MyService servicey()
    {
        System.out.println("servicey");
        return new MyService();
    }
    
    @Bean
    @ConditionalOnMissingBean // 已经存在 MyService类型的bean, 就不再创建 servicez
    public MyService servicez()
    {
        System.out.println("servicez");
        return new MyService();
    }
}
