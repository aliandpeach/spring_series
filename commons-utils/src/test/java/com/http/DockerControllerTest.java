package com.http;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import org.apache.commons.io.IOUtils;
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
    public void transferTest() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer";
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
}
