package com.yk.demo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * DemoService
 */
@Configuration
@ConditionalOnMissingBean(DemoSupport.class) // DemoSupport bean 不存在的时候才会有 DemoService
public class DemoService
{
    public void go()
    {
        System.out.println("go");
    }
    
    private MyService ms;
    
    /**
     * DemoService
     *
     * @param ms 多个 MyService bean 通过ObjectProvider 来解决出现的问题
     */
    public DemoService(ObjectProvider<MyService> ms)
    {
        this.ms = ms.orderedStream().findFirst().orElse(null);
    }
    
    @Bean
    public Map<String, String> waiting()
    {
        return new HashMap<>(Collections.singletonMap("waiting", "waiting"));
    }
    
    @Configuration
    public static class DemoAdapter
    {
        @Bean
        @ConditionalOnMissingBean
        public Map<String, String> adapter()
        {
            return new HashMap<>(Collections.singletonMap("k", "v"));
        }
    }
}
