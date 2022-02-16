package com.yk.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;

public class DynamicDataSource extends DruidDataSource
{
    private static final long serialVersionUID = 4059357116837066441L;

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
