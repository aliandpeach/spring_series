package com.http;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DockerControllerTest
{
    @Test
    public void queryTest() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/query";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "1");
        Object infos = httpClientUtil.get(url,
                new HashMap<>(), body,
                new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Object>() {}), 0);
        System.out.println(infos);
    }

    @Test
    public void uploadTest() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/upload";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        String infos = httpClientUtil.postFormData(url,
                new HashMap<>(),
                new HashMap<>(),
                Collections.singletonMap("upload_file", "D:\\env.txt"),
                new HttpClientUtil.StringResponseHandler());
        System.out.println(infos);
    }

    @Test
    public void transfer0Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/0";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        byte[] bytes = IOUtils.toByteArray(new FileInputStream("D:\\env.txt"));

        Map<String, Object> demo = new HashMap<>();
        demo.put("id", 11);
        demo.put("name", 12);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");

        byte[] infos = HttpFormDataUtil.postFormData(url,
                Collections.singletonMap("file", "D:\\env.txt"),
                Collections.singletonMap("indexModel", JSONUtil.toJsonStr(demo)),
                headers,
                proxyInfo,
                boundary,
                "application/json",
                new HttpFormDataUtil.HttpResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleHttpResponse(HttpFormDataUtil.HttpResponse<byte[]> response) throws IOException
                    {
                        int _code = response.getCode();
                        return IOUtils.toByteArray(response.getContent());
                    }
                });
        System.out.println(Arrays.equals(bytes, infos));
    }

    @Test
    public void transfer1Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/1";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        byte[] bytes = IOUtils.toByteArray(new FileInputStream("D:\\env.txt"));

        Map<String, String> demo = new HashMap<>();
        demo.put("id", "transfer1");
        demo.put("name", "transfer1");

        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");

        byte[] infos = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo)).postFormData(url,
                new HashMap<>(),
                demo,
                Collections.singletonMap("file", "D:\\env.txt"), new ResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleResponse(HttpResponse response) throws IOException
                    {
                        return IOUtils.toByteArray(response.getEntity().getContent());
                    }
                });
        System.out.println(Arrays.equals(bytes, infos));

        byte[] infos2 = HttpFormDataUtil.postFormData(url,
                Collections.singletonMap("file", "D:\\env.txt"),
                demo,
                new HashMap<>(),
                proxyInfo,
                UUID.randomUUID().toString().replace("-", ""),
                "text/plain",
                new HttpFormDataUtil.HttpResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleHttpResponse(HttpFormDataUtil.HttpResponse<byte[]> response) throws IOException
                    {
                        int _code = response.getCode();
                        return IOUtils.toByteArray(response.getContent());
                    }
                });
        System.out.println(Arrays.equals(bytes, infos2));
    }

    @Test
    public void transfer2Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/2";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        byte[] bytes = IOUtils.toByteArray(new FileInputStream("D:\\env.txt"));

        Map<String, Object> demo = new HashMap<>();
        demo.put("id", 11);
        demo.put("name", 12);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");

        byte[] infos = HttpFormDataUtil.postFormData(url,
                Collections.singletonMap("file", "D:\\env.txt"),
                Collections.singletonMap("indexModel", JSONUtil.toJsonStr(demo)),
                headers,
                proxyInfo,
                boundary,
                "application/json",
                new HttpFormDataUtil.HttpResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleHttpResponse(HttpFormDataUtil.HttpResponse<byte[]> response) throws IOException
                    {
                        int _code = response.getCode();
                        return IOUtils.toByteArray(response.getContent());
                    }
                });
        System.out.println(Arrays.equals(bytes, infos));
    }

    @Test
    public void transfer3Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/3";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        byte[] bytes = IOUtils.toByteArray(new FileInputStream("D:\\env.txt"));

        Map<String, String> demo = new HashMap<>();

        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");

        byte[] infos = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo)).postFormData(url,
                new HashMap<>(),
                demo,
                Collections.singletonMap("file", "D:\\env.txt"), new ResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleResponse(HttpResponse response) throws IOException
                    {
                        return IOUtils.toByteArray(response.getEntity().getContent());
                    }
                });
        System.out.println(Arrays.equals(bytes, infos));
    }
}
