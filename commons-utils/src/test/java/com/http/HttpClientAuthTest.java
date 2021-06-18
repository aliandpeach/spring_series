package com.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.yk.httprequest.HttpClientUtil.CONFIG_THREAD_LOCAL;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/17 17:51:36
 */
public class HttpClientAuthTest
{
    @Test
    public void httpGet()
    {
        HttpClientUtil.Config config = new HttpClientUtil.Config();
        config.setKeyStore("D:\\idea_workspace\\development_tool\\apache-tomcat-9.0.41_https\\conf\\ssl\\broker.ks");
        config.setKeyPasswd("Spinfo@0123");
        config.setKeyStorePasswd("Spinfo@0123");
        config.setType("JKS");
        CONFIG_THREAD_LOCAL.set(config);
        Map<String, String> result = HttpClientUtil.get("https://192.190.10.122:4533/index/download/v4",
                new HashMap<>(),
                new HashMap<>(Collections.singletonMap("key", "value1")),
                new TypeReference<Map<String, String>>() {}, 1);
        System.out.println(result);
    }
}
