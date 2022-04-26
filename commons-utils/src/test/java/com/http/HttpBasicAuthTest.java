package com.http;

import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class HttpBasicAuthTest
{
    private String URL_SECURED_BY_BASIC_AUTHENTICATION = "https://127.0.0.1:4433/hello";

    private String DEFAULT_USER = "user1";

    private String DEFAULT_PASS = "123456";

    /**
     * 标准模式
     */
    @Test
    public void CredentialsProvider() throws Exception
    {
        // 创建用户信息
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS);
        provider.setCredentials(AuthScope.ANY, credentials);

        // 创建客户端的时候进行身份验证
        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        HttpResponse response = client.execute(
                new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION));
        int statusCode = response.getStatusLine()
                .getStatusCode();
        Assert.assertEquals(statusCode, 200);
    }

    /**
     * 抢先模式
     */
    @Test
    public void PreemptiveBasicAuthentication() throws Exception
    {
        // 先进行身份验证
        HttpHost targetHost = new HttpHost("localhost", 8080, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS));

        AuthCache authCache = new BasicAuthCache();
        // 将身份验证放入缓存中
        authCache.put(targetHost, new BasicScheme());

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(
                new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION), context);

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 200);
    }

    /**
     * 原生 Http Basic 模式
     */
    @Test
    public void HttpBasicAuth() throws Exception
    {
        HttpGet request = new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION);
        // 手动构建验证信息
        String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        // 将验证信息放入到 Header
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(statusCode, 200);
    }
}
