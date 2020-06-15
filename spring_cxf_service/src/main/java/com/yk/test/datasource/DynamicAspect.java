package com.yk.test.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

@Aspect
@Service
//<aop:aspectj-autoproxy proxy-target-class="true"/>
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DynamicAspect
{
    public DynamicAspect()
    {
    }

    @Pointcut("@annotation(com.yk.test.datasource.Dynamic)")
    public void foundDynamicMethed()
    {
    }

    @Before(value = "foundDynamicMethed()")
    public void foundBefore(JoinPoint joinPoint)
    {
        Object[] args = joinPoint.getArgs();
        if (null == args || args.length == 0)
        {
            return;
        }
        Object first = args[0];
        if (!(first instanceof String))
        {
            return;
        }
        String id = String.valueOf(first);
        ParamHolder.getInstance().setThreadLocalParam(id);
    }

    @AfterReturning("foundDynamicMethed()")
    public void foundAfterReturning(JoinPoint joinPoint)
    {
        System.out.println(joinPoint);
    }

    @AfterThrowing("foundDynamicMethed()")
    public void foundAfterThrowing(JoinPoint joinPoint)
    {
        System.out.println(joinPoint);
    }

}
