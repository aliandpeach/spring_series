package com.yk.httprequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpConnection
{
    /**
     * get请求
     *
     * @param url
     * @param param
     * @return
     */
    public static String get(String url, Map<String, Object> param)
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, Object> entry : param.entrySet())
            {
                params.append(entry.getKey());
                params.append("=");
                params.append(entry.getValue().toString());
                params.append("&");
            }
            if (params.length() > 0)
            {
                params.deleteCharAt(params.lastIndexOf("&"));
            }
            URL restServiceURL = new URL(url + (params.length() > 0 ? "?" + params.toString() : ""));
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if (httpConnection.getResponseCode() != 200)
            {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }
            InputStream inStrm = httpConnection.getInputStream();
            byte[] b = new byte[1024];
            int length = -1;
            while ((length = inStrm.read(b)) != -1)
            {
                builder.append(new String(b, 0, length));
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * post    请求
     *
     * @param url
     * @param param
     * @return
     */
    public static String post(String url, Map<String, Object> param)
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, Object> entry : param.entrySet())
            {
                params.append(entry.getKey());
                params.append("=");
                params.append(entry.getValue().toString());
                params.append("&");
            }
            if (params.length() > 0)
            {
                params.deleteCharAt(params.lastIndexOf("&"));
            }
            URL restServiceURL = new URL(url + (params.length() > 0 ? "?" + params.toString() : ""));
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpConnection.setDoInput(true);
            // Post 请求不能使用缓存
            httpConnection.setUseCaches(false);
            if (httpConnection.getResponseCode() != 200)
            {
                throw new RuntimeException("HTTP POST Request Failed with Error code : "
                        + httpConnection.getResponseCode());
            }
            InputStream inStrm = httpConnection.getInputStream();
            byte[] b = new byte[1024];
            int length = -1;
            while ((length = inStrm.read(b)) != -1)
            {
                builder.append(new String(b, 0, length));
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    public static void downloadHttpsCert()
    {
    
    }
}
