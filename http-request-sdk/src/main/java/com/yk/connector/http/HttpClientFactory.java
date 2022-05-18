package com.yk.connector.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 初始化 HttpClient
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public class HttpClientFactory
{

    public static final HttpClientFactory INSTANCE = new HttpClientFactory();

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFactory.class);

    private CloseableHttpClient client;

    /**
     * Creates or Returns an existing HttpClient
     *
     * @param config the configuration
     * @return CloseableHttpClient
     */
    CloseableHttpClient getClient(Config config)
    {
        if (client == null)
        {
            synchronized (this)
            {
                if (client == null)
                {
                    client = buildClient(config);
                }
            }
        }
        return client;
    }

    private CloseableHttpClient buildClient(Config config)
    {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        if (null != config.getProxy() && config.getProxy().isDefaultProxy())
        {
            try
            {
                URL url = new URL(config.getProxy().getHost());
                HttpHost proxy = new HttpHost(url.getHost(), config.getProxy().getPort(), url.getProtocol());
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                clientBuilder.setRoutePlanner(routePlanner).setProxy(proxy);
            }
            catch (MalformedURLException e)
            {
                LOG.error(e.getMessage(), e);
            }
        }
        if (null != config.getProxy() && config.getProxy().isCredentials())
        {
            try
            {
                URL url = new URL(config.getProxy().getHost());
                HttpHost httpHost = new HttpHost(config.getProxy().getHost(), config.getProxy().getPort(), url.getProtocol());
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(config.getProxy().getUsername(), config.getProxy().getPassword()));
                clientBuilder.setDefaultCredentialsProvider(provider);
            }
            catch (MalformedURLException e)
            {
                LOG.error(e.getMessage(), e);
            }
        }

        if (config.isIgnoreSSLVerification())
        {
            clientBuilder.setSSLContext(UntrustedSSL.getSSLContext());
            clientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        }
        else
        {
            clientBuilder.setSSLContext(UntrustedSSL.getTrustedSSLContext());
            clientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        }

        if (config.getSslContext() != null)
        {
            clientBuilder.setSSLContext(config.getSslContext());
        }

        if (config.getMaxConnections() > 0)
        {
            clientBuilder.setMaxConnTotal(config.getMaxConnections());
        }

        if (config.getMaxConnectionsPerRoute() > 0)
        {
            clientBuilder.setMaxConnPerRoute(config.getMaxConnectionsPerRoute());
        }

        // 配置连接超时和响应超时
        RequestConfig.Builder rcb = RequestConfig.custom();

        if (config.getConnectTimeout() > 0)
        {
            rcb.setConnectTimeout(config.getConnectTimeout());
        }

        if (config.getReadTimeout() > 0)
        {
            rcb.setSocketTimeout(config.getReadTimeout());
        }

        return clientBuilder.setDefaultRequestConfig(rcb.build()).build();
    }
}
