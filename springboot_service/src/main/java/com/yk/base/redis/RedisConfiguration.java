package com.yk.base.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfiguration
 */
@Configuration
public class RedisConfiguration
{
    /**
     * 一般不设置 hashValueSerializer, 值默认是被 JdkSerializationRedisSerializer (或者其他例如GenericJackson2JsonRedisSerializer) 自动序列化,
     * 		但是increment自增进去的的不会自动序列化(就是一个数字没有额外序列化信息), 导致取值的时候无法反序列化而异常, 必须设置为StringRedisSerializer,
     * 		但是设置hashValueSerializer=StringRedisSerializer又会导致所有的hash操作写入值都必须是String类型 (其实valueSerializer其实也是一样的)
     * 		因此, 还是不要使用increment操作了,
     * 		optForValue().increment后直接会返回结果, 因此可以通过.increment("xxx", 0)返回此时的实际数值,
     * 		但是optForHash()如果存在多个hashKey, 只能通过获取所有hashKey, 然后循环中去获取, 更麻烦  -->
     */
    @Bean
    @ConditionalOnClass(LettuceConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory)
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
