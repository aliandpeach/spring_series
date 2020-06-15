package com.yk.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;

public class DynamicDataSource extends DruidDataSource
{
    @Override
    public DruidPooledConnection getConnection() throws SQLException
    {
        try
        {
            return MyDruidConnectionHolder.getInstance().getConnection(this);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return getConnection(maxWait);
    }
}
