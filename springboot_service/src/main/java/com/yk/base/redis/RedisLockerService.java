package com.yk.base.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 分布式redis锁
 */
@Service
@Slf4j
public class RedisLockerService
{
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisLockerService(RedisTemplate<String, String> redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }
}
