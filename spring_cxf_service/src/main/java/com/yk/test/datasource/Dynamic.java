package com.yk.test.datasource;


import java.lang.annotation.*;

/**
 * 动态数据源 注解
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dynamic
{
}
