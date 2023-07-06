package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
//import org.springframework.session.data.redis.RedisIndexedSessionRepository;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * RedisSessionConfiguration 自动配置会产生bean冲突问题
 *
 * 如果使用 @EnableSpringHttpSession  需要自定义sessionRepository-bean 根据条件RedisSessionConfiguration就不会自动配置
 * 或使用 @EnableRedisHttpSession 不用自定义sessionRepository-bean (会自动加载)  根据条件RedisSessionConfiguration也就不会自动配置
 *
 * 或者什么都不配置 RedisSessionConfiguration 自动配置会生效 (相当于配置 @EnableRedisHttpSession)
 */
//@EnableSpringHttpSession
//@EnableRedisHttpSession
@Configuration
public class SpringHttpSessionConfig
{
//    @Bean
//    public RedisIndexedSessionRepository sessionRepository(RedisTemplate<Object, Object> redisTemplate)
//    {
//        return new RedisIndexedSessionRepository(redisTemplate);
//    }
}
