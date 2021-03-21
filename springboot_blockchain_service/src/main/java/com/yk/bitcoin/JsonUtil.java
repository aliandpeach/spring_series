package com.yk.bitcoin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JsonUtil
{

    public static <T> T fromJsonFilter(String json, Class<T> clazz, String... params)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();

            String[] beanProperties = params;
            String nonPasswordFilterName = "myFilter";//需要跟 TestUser类上的注解@JsonFilter("myFilter")里面的一致
            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter(nonPasswordFilterName, SimpleBeanPropertyFilter.serializeAllExcept(beanProperties));
            //serializeAllExcept 表示序列化全部，除了指定字段
            //filterOutAllExcept 表示过滤掉全部，除了指定的字段
            objectMapper.setFilterProvider(filterProvider);
            T result = objectMapper.readValue(json, clazz);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonFilter(String json, TypeReference<T> type, String... params)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String[] beanProperties = params;
            String nonPasswordFilterName = "myFilter";//需要跟TestUser类上的注解@JsonFilter("myFilter")里面的一致
            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter(nonPasswordFilterName, SimpleBeanPropertyFilter.serializeAllExcept(beanProperties));
            //serializeAllExcept 表示序列化全部，除了指定字段
            //filterOutAllExcept 表示过滤掉全部，除了指定的字段
            objectMapper.setFilterProvider(filterProvider);
            T result = objectMapper.readValue(json, type);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            T result = objectMapper.readValue(json, clazz);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> type)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            T result = objectMapper.readValue(json, type);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
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

    public static class CurTypeReference2<T> extends TypeReference<T>
    {

        public CurTypeReference2()
        {
            super();
        }
    }
}
