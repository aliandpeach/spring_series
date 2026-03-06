package com.yk.test.dynamic;

import com.yk.test.datasource.ParamHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DataSourceAspect
{

    /**
     * UserServiceFirst使用数据源1
     */
//    @Before("execution(* com.yk.test.service.UserServiceFirst.*(..))")
//    public void setFirstDataSource()
//    {
//        log.info("Set DataSource:{}", DatabaseType.FIRST_MYSQL.name());
//        DataSourceHolder.setDatabaseType(DatabaseType.FIRST_MYSQL);
//    }

    /**
     * UserServiceFirst使用数据源2
     */
//    @Before("execution(* com.yk.test.service.UserServiceSecond.*(..))")
//    public void setSecondDataSource()
//    {
//        log.info("Set DataSource:{}", DatabaseType.SECOND_MYSQL.name());
//        DataSourceHolder.setDatabaseType(DatabaseType.SECOND_MYSQL);
//    }

    public DataSourceAspect()
    {
    }

    @Pointcut("@annotation(com.yk.test.dynamic.Dynamic2)")
    public void foundDynamicMethod()
    {
    }

    @Before(value = "foundDynamicMethod()")
    public void foundBefore(JoinPoint joinPoint)
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Dynamic2 myAnnotation = method.getAnnotation(Dynamic2.class);

        if (myAnnotation != null) {
            DatabaseType databaseType = myAnnotation.value();
            System.out.println("Annotation value: " + databaseType.name());
            DataSourceHolder.setDatabaseType(databaseType);
        }else{
            DataSourceHolder.setDatabaseType(DatabaseType.FIRST_MYSQL);
        }
    }

    @AfterReturning("foundDynamicMethod()")
    public void foundAfterReturning(JoinPoint joinPoint)
    {
        System.out.println(joinPoint);
    }

    @AfterThrowing("foundDynamicMethod()")
    public void foundAfterThrowing(JoinPoint joinPoint)
    {
        System.out.println(joinPoint);
    }
}
