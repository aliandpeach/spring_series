package com.http;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
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
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String hex = HexUtil.encodeHexStr(bytes);
        System.out.println(hex.toUpperCase(Locale.ROOT));
        new SecureRandom().nextBytes(bytes);
        hex = HexUtil.encodeHexStr(bytes);
        System.out.println(hex.toUpperCase(Locale.ROOT));

        HttpClientUtil.Config config = new HttpClientUtil.Config();
        config.setKeyStore("D:\\idea_workspace\\development_tool\\apache-tomcat-9.0.41_https\\conf\\ssl\\broker.ks");
        config.setKeyPasswd("Spinfo@0123");
        config.setKeyStorePasswd("Spinfo@0123");
        config.setType("JKS");
        config.setSslKeyManager(true);
        CONFIG_THREAD_LOCAL.set(config);
        Map<String, String> param = new HashMap<>(Collections.singletonMap("jobId", "jobId"));
        param.put("id", "id");
        param.put("name", "name");
        Map<String, String> result = HttpClientUtil.get("https://192.190.116.205:443/SIMP_DBS_S/event/file/analysis/analyze",
                new HashMap<>(),
                param,
                new TypeReference<Map<String, String>>()
                {
                }, 1);
        System.out.println(result);
    }

    boolean isExclusion(String requestUri, HashSet<String> excludesPattern, String contextPath)
    {
        String uri = "/";
        if (null != excludesPattern && null != requestUri)
        {
            if (null != contextPath && requestUri.startsWith(contextPath))
            {
                requestUri = requestUri.substring(contextPath.length());
                if (!requestUri.startsWith(uri))
                {
                    requestUri = "/".concat(requestUri);
                }
            }
            Iterator iterator = excludesPattern.iterator();
            String pattern;
            do
            {
                if (!iterator.hasNext())
                {
                    return false;
                }
                pattern = (String) iterator.next();
            }
            while (!this.matches(pattern, requestUri));
            return true;
        }
        else
        {
            return false;
        }
    }

    String PATTERN_ALL = "*";

    boolean matches(String pattern, String source)
    {
        if (null != pattern && null != source)
        {
            pattern = pattern.trim();
            source = source.trim();
            int start;
            if (pattern.endsWith(PATTERN_ALL))
            {
                start = pattern.length() - 1;
                return source.length() >= start && pattern.substring(0, start).equals(source.substring(0, start));
            }
            else if (pattern.startsWith(PATTERN_ALL))
            {
                start = pattern.length() - 1;
                return source.length() >= start && source.endsWith(pattern.substring(1));
            }
            else if (pattern.contains(PATTERN_ALL))
            {
                start = pattern.indexOf(PATTERN_ALL);
                int end = pattern.lastIndexOf(PATTERN_ALL);
                return source.startsWith(pattern.substring(0, start)) && source.endsWith(pattern.substring(end + 1));
            }
            else
            {
                return pattern.equals(source);
            }
        }
        else
        {
            return false;
        }
    }
}
