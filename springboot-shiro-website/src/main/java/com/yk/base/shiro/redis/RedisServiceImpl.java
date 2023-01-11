package com.yk.base.shiro.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("redisService")
public class RedisServiceImpl
{
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加opsForValue
     *
     * @param key   键
     * @param value 值
     */
    public void addValue(String key, Object value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加opsForValue
     *
     * @param key     键
     * @param value   值
     * @param timeout 缓存时长
     * @param unit    缓存时长类型
     */
    public void addValue(String key, Object value, long timeout, TimeUnit unit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 添加opsForHash
     *
     * @param key     键
     * @param value   值
     * @param timeout 缓存时长
     * @param unit    缓存时长类型
     */
    public void addHash(String key, Object hashKey, Object value, long timeout, TimeUnit unit)
    {
        redisTemplate.opsForHash().put(key, hashKey, value);
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 通过key删除缓存
     *
     * @param key      键
     * @param hashKeys hash键
     */
    public void deleteHashByKeys(String key, String... hashKeys)
    {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 通过key删除缓存
     *
     * @param keys 缓存键
     */
    public void deleteByKeys(Object... keys)
    {
        this.deleteByKeys(Arrays.stream(keys).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList()));
    }

    /**
     * 通过key删除缓存
     *
     * @param keys 缓存键
     */
    public void deleteByKeys(Collection<String> keys)
    {
        redisTemplate.delete(keys);
    }

    /**
     * 通过key删除缓存
     *
     * @param prefix 模糊前缀
     */
    public void deleteByPrefix(String prefix)
    {
        Optional.ofNullable(redisTemplate.keys(prefix.concat("*"))).ifPresent(t -> redisTemplate.delete(t));
    }

    /**
     * 通过key删除缓存
     *
     * @param suffix 模糊后缀
     */
    public void deleteBySuffix(String suffix)
    {
        Optional.ofNullable(redisTemplate.keys("*".concat(suffix))).ifPresent(t -> redisTemplate.delete(t));
    }

    /**
     * 是否包含key
     *
     * @param key 键
     * @return 是否包含
     */
    public boolean hasKey(String key)
    {
        return Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false);
    }

    /**
     * 是否包含key
     *
     * @param key     键
     * @param hashKey hash键
     * @return 是否包含
     */
    public boolean hasHashKey(String key, Object hashKey)
    {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * size数量
     *
     * @param prefix 模糊前缀
     * @return 数量
     */
    public Long getSizeByPrefix(Object prefix)
    {
        return (long) this.getKeysByPrefix((String) prefix).size();
    }

    /**
     * size数量
     *
     * @param key 键
     * @return 数量
     */
    public Long getHashSize(String key)
    {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 通过key获取缓存键
     *
     * @param prefix 模糊前缀
     * @return 缓存键集合
     */
    public Set<String> getKeysByPrefix(String prefix)
    {
        return redisTemplate.keys(prefix.concat("*"));
    }

    /**
     * 通过key获取缓存键
     *
     * @param suffix 模糊后缀
     * @return 缓存键集合
     */
    public Set<String> getKeysBySuffix(String suffix)
    {
        return redisTemplate.keys("*".concat(suffix));
    }

    /**
     * 通过键获取值
     *
     * @param key 键
     * @return 值
     */
    public Object getValue(Object key)
    {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 通过键获取值
     *
     * @param key     键
     * @param hashKey hash键
     * @return 值
     */
    public Object getHashValue(String key, Object hashKey)
    {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 通过key获取剩余时间
     *
     * @param key  键
     * @param unit 时间类型
     * @return
     */
    public Long getExpire(String key, TimeUnit unit)
    {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 设置失效时间
     *
     * @param key     键
     * @param timeout 失效时间
     * @param unit    时间类型
     */
    public void setExpire(String key, long timeout, TimeUnit unit)
    {
        redisTemplate.expire(key, timeout, unit);
    }
}
