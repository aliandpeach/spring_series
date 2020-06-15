package com.yk.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyDruidConnectionHolder
{
    private MyDruidConnectionHolder()
    {

    }

    public static MyDruidConnectionHolder getInstance()
    {
        return MyDruidConnectionHolderInstance.INSTANCE;
    }

    //不适用ConCurrentHashMap是因为其只支持最大16条线程
    private Map<String, MyDruidDataSource> dataSourceMap = Collections.synchronizedMap(new HashMap());

    public synchronized DruidPooledConnection getConnection(DruidDataSource druidDataSource)
            throws IllegalAccessException, SQLException
    {
        if (null != ParamHolder.getInstance().getThreadLocalParam() && dataSourceMap.containsKey(ParamHolder.getInstance().getThreadLocalParam()))
        {
            return dataSourceMap.get(ParamHolder.getInstance().getThreadLocalParam()).getConnection();
        }

        MyDruidDataSource myDruidDataSource = new MyDruidDataSource();
        Field[] fields = druidDataSource.getClass().getSuperclass().getDeclaredFields();
        Field[] fields2 = druidDataSource.getClass().getDeclaredFields();
        Field[] fields3 = druidDataSource.getClass().getSuperclass().getSuperclass().getDeclaredFields();
        //通过反射获取新的DruidDataSource
        copy(druidDataSource, myDruidDataSource, fields);
        copy(druidDataSource, myDruidDataSource, fields2);
        copy(druidDataSource, myDruidDataSource, fields3);
        String orginalUrl = druidDataSource.getUrl();
        myDruidDataSource.setUrl(String.format(orginalUrl, ParamHolder.getInstance().getThreadLocalParam()));
        myDruidDataSource.setLastUpdateTimeMillis(System.currentTimeMillis());
        dataSourceMap.put(ParamHolder.getInstance().getThreadLocalParam(), myDruidDataSource);
        return myDruidDataSource.getConnection();
    }

    private static void copy(DruidDataSource druidDataSource, MyDruidDataSource myDruidDataSource, Field[] fields3) throws IllegalAccessException
    {
        for (Field field : fields3)
        {
            if (field == null)
            {
                continue;
            }
            if (!Modifier.isFinal(field.getModifiers()))
            {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Object value = field.get(druidDataSource);
                field.set(myDruidDataSource, value);
                field.setAccessible(accessible);
            }
        }
    }

    private static class MyDruidConnectionHolderInstance
    {
        public static MyDruidConnectionHolder INSTANCE = new MyDruidConnectionHolder();
    }
}
