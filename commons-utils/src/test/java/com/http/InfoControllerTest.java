package com.http;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfoControllerTest
{
    @Test
    public void downloadTest() throws Exception
    {
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        String url = "https://192.168.31.158:4433/rest/download";
        Map<String, Object> body = new HashMap<>();
//        body.put("jobId", UUID.randomUUID().toString());
//        body.put("url", "https://192.168.31.158:31111/rest/transfer/3");
        Object result = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo))
                .post(url,
                        Collections.singletonMap("token", "123"),
                        JSONUtil.toJsonStr(body),
                        new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Object>() {}));
        System.out.println(result);
    }
}
