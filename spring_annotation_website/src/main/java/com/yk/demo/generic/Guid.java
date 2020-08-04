package com.yk.demo.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Guid<T extends Builder> extends Generate<T> {
    public void build() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType =( (ParameterizedType) type);
        Type ts[] = parameterizedType.getActualTypeArguments();
        System.out.println(ts);
    }
}
