package com.yk.base.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/17 11:15:50
 */
public class CustomRedisSerializer implements RedisSerializer<Object>
{

    private Converter<Object, byte[]> serializer = new SerializingConverter();
    private Converter<byte[], Object> deserializer = new DeserializingConverter();

    static final byte[] EMPTY_ARRAY = new byte[0];

    @Override
    public Object deserialize(byte[] bytes)
    {
        if (isEmpty(bytes))
        {
            return null;
        }

        try
        {
            return deserializer.convert(bytes);
        }
        catch (Exception ex)
        {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

    @Override
    public byte[] serialize(Object object)
    {
        if (object == null)
        {
            return EMPTY_ARRAY;
        }
        try
        {
            return serializer.convert(object);
        }
        catch (Exception ex)
        {
            return EMPTY_ARRAY;
        }
    }

    private boolean isEmpty(byte[] data)
    {
        return (data == null || data.length == 0);
    }
}
