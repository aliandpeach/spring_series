package com.yk.connector.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Config 4 HttpClient
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public final class Config
{
    public static final Config DEFAULT = new Config();
    private int connectTimeout;
    private int readTimeout;
    private SSLContext sslContext;
    private HostnameVerifier hostNameVerifier;
    private boolean ignoreSSLVerification = true;
    private String natHostOrIP;
    private int maxConnections;
    private int maxConnectionsPerRoute;
    private ProxyHost proxy;

    private Config()
    {
    }

    public static Config newConfig()
    {
        return new Config();
    }

    public Config withConnectionTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Config withReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
        return this;
    }

    public Config withSSLContext(SSLContext sslContext)
    {
        this.sslContext = sslContext;
        return this;
    }

    public Config withMaxConnections(int maxConnections)
    {
        this.maxConnections = maxConnections;
        return this;
    }

    public Config withMaxConnectionsPerRoute(int maxConnectionsPerRoute)
    {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        return this;
    }

    public Config withProxy(ProxyHost proxy)
    {
        this.proxy = proxy;
        return this;
    }

    public Config withEndpointNATResolution(String natHostOrIP)
    {
        this.natHostOrIP = natHostOrIP;
        return this;
    }

    public Config withHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        this.hostNameVerifier = hostnameVerifier;
        return this;
    }

    public Config withSSLVerificationDisabled()
    {
        this.ignoreSSLVerification = Boolean.TRUE;
        return this;
    }

    public Config withSSLVerificationEnabled()
    {
        this.ignoreSSLVerification = Boolean.FALSE;
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + connectTimeout;
        result = prime * result + maxConnections;
        result = prime * result + maxConnectionsPerRoute;
        result = prime * result + (ignoreSSLVerification ? 1231 : 1237);
        result = prime * result + ((natHostOrIP == null) ? 0 : natHostOrIP.hashCode());
        result = prime * result + readTimeout;
        result = prime * result + ((proxy == null) ? 0 : proxy.hashCode());
        result = prime * result + ((sslContext == null) ? 0 : sslContext.hashCode());
        result = prime * result + ((hostNameVerifier == null) ? 0 : hostNameVerifier.hashCode());
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
        Config other = (Config) obj;
        if (connectTimeout != other.connectTimeout)
            return false;
        if (maxConnections != other.maxConnections)
            return false;
        if (maxConnectionsPerRoute != other.maxConnectionsPerRoute)
            return false;
        if (ignoreSSLVerification != other.ignoreSSLVerification)
            return false;
        if (natHostOrIP == null)
        {
            if (other.natHostOrIP != null)
                return false;
        }
        else if (!natHostOrIP.equals(other.natHostOrIP))
            return false;
        if (readTimeout != other.readTimeout)
            return false;
        if (proxy == null)
        {
            if (other.proxy != null)
                return false;
        }
        else if (!proxy.equals(other.proxy))
            return false;
        if (sslContext == null)
        {
            if (other.getSslContext() != null)
            {
                return false;
            }
        }
        else if (!sslContext.equals(other.getSslContext()))
        {
            return false;
        }
        if (hostNameVerifier == null)
        {
            if (other.getHostNameVerifier() != null)
            {
                return false;
            }
        }
        else if (!hostNameVerifier.equals(other.getHostNameVerifier()))
        {
            return false;
        }
        return true;
    }

    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    public int getReadTimeout()
    {
        return readTimeout;
    }

    public SSLContext getSslContext()
    {
        return sslContext;
    }

    public HostnameVerifier getHostNameVerifier()
    {
        return hostNameVerifier;
    }

    public boolean isIgnoreSSLVerification()
    {
        return ignoreSSLVerification;
    }

    public String getNatHostOrIP()
    {
        return natHostOrIP;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public int getMaxConnectionsPerRoute()
    {
        return maxConnectionsPerRoute;
    }

    public ProxyHost getProxy()
    {
        return proxy;
    }
}
