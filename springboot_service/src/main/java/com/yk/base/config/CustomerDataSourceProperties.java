package com.yk.base.config;

public class CustomerDataSourceProperties
{
    private String type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private HikariProperties hikari;

    // Getters and Setters
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDriverClassName()
    {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName)
    {
        this.driverClassName = driverClassName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public HikariProperties getHikari()
    {
        return hikari;
    }

    public void setHikari(HikariProperties hikari)
    {
        this.hikari = hikari;
    }

    public static class HikariProperties
    {
        private int maximumPoolSize;
        private int minimumIdle;
        private long idleTimeout;
        private long connectionTimeout;
        private long maxLifetime;
        private String connectionTestQuery;
        private String poolName;
        private boolean autoCommit;

        // Getters and Setters
        public int getMaximumPoolSize()
        {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize)
        {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getMinimumIdle()
        {
            return minimumIdle;
        }

        public void setMinimumIdle(int minimumIdle)
        {
            this.minimumIdle = minimumIdle;
        }

        public long getIdleTimeout()
        {
            return idleTimeout;
        }

        public void setIdleTimeout(long idleTimeout)
        {
            this.idleTimeout = idleTimeout;
        }

        public long getConnectionTimeout()
        {
            return connectionTimeout;
        }

        public void setConnectionTimeout(long connectionTimeout)
        {
            this.connectionTimeout = connectionTimeout;
        }

        public long getMaxLifetime()
        {
            return maxLifetime;
        }

        public void setMaxLifetime(long maxLifetime)
        {
            this.maxLifetime = maxLifetime;
        }

        public String getConnectionTestQuery()
        {
            return connectionTestQuery;
        }

        public void setConnectionTestQuery(String connectionTestQuery)
        {
            this.connectionTestQuery = connectionTestQuery;
        }

        public String getPoolName()
        {
            return poolName;
        }

        public void setPoolName(String poolName)
        {
            this.poolName = poolName;
        }

        public boolean isAutoCommit()
        {
            return autoCommit;
        }

        public void setAutoCommit(boolean autoCommit)
        {
            this.autoCommit = autoCommit;
        }
    }
}
