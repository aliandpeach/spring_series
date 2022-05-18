package com.yk.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/08 16:19:11
 */
public class JSONUtil
{
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = objectMapper.readValue(json, clazz);
        return result;
    }

    public static <T> T fromJson(String json, TypeReference<T> type) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = objectMapper.readValue(json, type);
        return result;
    }

    public static <T> T fromJson(InputStream json, TypeReference<T> type) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = objectMapper.readValue(json, type);
        return result;
    }

    public static <T> T fromJson(String json, JavaType type) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = objectMapper.readValue(json, type);
        return result;
    }

    public static String toJson(Object object) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    public static class CurParameterizedType implements ParameterizedType
    {
        private Class<?> clazz;

        private Type[] types;

        public CurParameterizedType(Class<?> clazz, Type[] types)
        {
            this.clazz = clazz;
            this.types = types;
        }

        public Type[] getActualTypeArguments()
        {
            return null == types ? new Type[0] : types;
        }

        public Type getRawType()
        {
            return clazz;
        }

        public Type getOwnerType()
        {
            return clazz;
        }
    }

    public static class CurTypeReference<T> extends TypeReference<T>
    {

        private CurParameterizedType curParameterizedType;

        public CurTypeReference(CurParameterizedType curParameterizedType)
        {
            super();
            this.curParameterizedType = curParameterizedType;
        }

        public Type getType()
        {
            return curParameterizedType;
        }
    }
}
