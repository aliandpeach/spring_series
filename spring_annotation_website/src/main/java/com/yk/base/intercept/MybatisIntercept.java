package com.yk.base.intercept;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * MybatisIntercept
 */

@Component
public class MybatisIntercept implements Interceptor
{
    @Override
    public Object intercept(Invocation invocation)
    {
        return null;
    }
    
    @Override
    public Object plugin(Object target)
    {
        return null;
    }
    
    @Override
    public void setProperties(Properties properties)
    {
    
    }
}
