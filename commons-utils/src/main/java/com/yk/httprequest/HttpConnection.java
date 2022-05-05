package com.yk.httprequest;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpConnection
{
    /**
     * get 请求
     */
    public static HttpFormDataUtil.HttpResponse get(String url, Map<String, Object> param) throws IOException
    {
        String params = param.entrySet().stream().map(t -> t.getKey() + "=" + t.getValue()).collect(Collectors.joining("&"));
        URL restServiceURL = new URL(url + (params.length() > 0 ? "?" + params : ""));
        HttpsURLConnection httpsConnection = (HttpsURLConnection) restServiceURL.openConnection();
        httpsConnection.setHostnameVerifier((s, sslSession) -> true);
        httpsConnection.setSSLSocketFactory(createSSLContext().getSocketFactory());

        httpsConnection.setRequestMethod("GET");
        httpsConnection.setDoInput(true);
        httpsConnection.setRequestProperty("Accept", "application/json");

        httpsConnection.connect();
        if (httpsConnection.getResponseCode() != 200)
        {
            throw new RuntimeException("HTTP GET Request Failed with Error code : "
                    + httpsConnection.getResponseCode());
        }
        return HttpFormDataUtil.getHttpResponse(httpsConnection);
    }

    /**
     * post 请求
     */
    public static HttpFormDataUtil.HttpResponse post(String url, Map<String, Object> param) throws IOException
    {
        URL restServiceURL = new URL(url);
        HttpsURLConnection httpsConnection = (HttpsURLConnection) restServiceURL.openConnection();
        httpsConnection.setHostnameVerifier((s, sslSession) -> true);
        httpsConnection.setSSLSocketFactory(createSSLContext().getSocketFactory());

        httpsConnection.setRequestMethod("POST");
        httpsConnection.setRequestProperty("Accept", "application/json");
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        httpsConnection.setDoInput(true);
        httpsConnection.setDoOutput(true);
        // POST 请求不能使用缓存
        httpsConnection.setUseCaches(false);

        httpsConnection.connect();
        if (httpsConnection.getResponseCode() != 200)
        {
            throw new RuntimeException("HTTP POST Request Failed with Error code : "
                    + httpsConnection.getResponseCode());
        }

        String body = JSONUtil.toJson(param);
        try (OutputStream outputStream = httpsConnection.getOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(outputStreamWriter);
             StringReader reader = new StringReader(body))
        {
            char[] buffer = new char[4096];
            int len;
            while ((len = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, len);
            }
        }
        return HttpFormDataUtil.getHttpResponse(httpsConnection);
    }

    public static void downloadHttpsCert()
    {

    }

    public static SSLContext createSSLContext()
    {
        SSLContext sslContext = null;
        try
        {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{new X509TrustManager()
            {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sslContext;
    }
}
