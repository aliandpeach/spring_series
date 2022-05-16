package com.yk.base.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * RedisConfiguration
 */
@Configuration
public class RedisConfiguration
{
    @Bean
    @ConditionalOnBean(LettuceConnectionFactory.class)
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory connectionFactory)
    {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnBean(LettuceConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate()
    {
        return new StringRedisTemplate();
    }
}
