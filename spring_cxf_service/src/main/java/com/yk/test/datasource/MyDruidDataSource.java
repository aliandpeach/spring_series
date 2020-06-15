package com.yk.test.datasource;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 覆盖DruidDataSource
 */
public class MyDruidDataSource extends DruidDataSource
{

    private long lastUpdateTimeMillis;

    public long getLastUpdateTimeMillis()
    {
        return lastUpdateTimeMillis;
    }

    public void setLastUpdateTimeMillis(long lastUpdateTimeMillis)
    {
        this.lastUpdateTimeMillis = lastUpdateTimeMillis;
    }
}
