package com.yk.connector.http;

/**
 * ProxyHost代理
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public final class ProxyHost
{
    private String host;

    private int port;

    private String username;

    private String password;

    public ProxyHost(String host, int port)
    {
        this(host, port, null, null);
    }

    public ProxyHost(String host, int port, String username, String password)
    {
        super();
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 普通代理
     *
     * @param host the proxy host (ex. http://127.0.0.1)
     * @param port the proxy port (ex. 8080)
     * @return ProxyHost
     */
    public static ProxyHost of(String host, int port)
    {
        return new ProxyHost(host, port);
    }

    /**
     * 用户名密码代理
     *
     * @param host     the proxy host (ex. http://127.0.0.1)
     * @param port     the proxy port (ex. 8080)
     * @param username the username for proxy authentication
     * @param password the password for proxy authentication
     * @return ProxyHost
     */
    public static ProxyHost of(String host, int port, String username, String password)
    {
        return new ProxyHost(host, port, username, password);
    }

    public String getHostWithPort()
    {
        return String.format("%s:%d", host, port);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + port;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProxyHost other = (ProxyHost) obj;
        if (host == null)
        {
            if (other.host != null)
                return false;
        }
        else if (!host.equals(other.host))
            return false;
        if (password == null)
        {
            if (other.password != null)
                return false;
        }
        else if (!password.equals(other.password))
            return false;
        if (port != other.port)
            return false;
        if (username == null)
        {
            if (other.username != null)
                return false;
        }
        else if (!username.equals(other.username))
            return false;
        return true;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean isDefaultProxy()
    {
        return null != host && port > 0 && port < 65535 && (null == username || null == password);
    }

    public boolean isCredentials()
    {
        return null != host && port > 0 && port < 65535 && null != username && null != password;
    }
}
