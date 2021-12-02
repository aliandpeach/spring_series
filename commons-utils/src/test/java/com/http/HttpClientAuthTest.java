package com.http;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

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
    public void httpGet() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
    {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String hex = HexUtil.encodeHexStr(bytes);
        System.out.println(hex.toUpperCase(Locale.ROOT));
        new SecureRandom().nextBytes(bytes);
        hex = HexUtil.encodeHexStr(bytes);
        System.out.println(hex.toUpperCase(Locale.ROOT));

        HttpClientUtil.Config config = new HttpClientUtil.Config();
        config.setKeyStore("D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\spinfossl\\sdk.ks");
        config.setKeyPasswd("C47182A19F40F69B5C022666B72A751CnHXB$f#T");
        config.setKeyStorePasswd("E47CF21F723705F516F5F2F0068001FE@m$5Sq4Q");
        config.setType("JKS");
        config.setSslKeyManager(true);
        Map<String, String> param = new HashMap<>(Collections.singletonMap("jobId", "jobId"));
        param.put("id", "id");
        param.put("name", "name");
        Map<String, String> result = new HttpClientUtil(config).get("https://192.190.116.205:443/SIMP_DBS_S/event/file/analysis/analyze",
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

    @Test
    public void testPostText() throws Exception
    {
        File f = new File("C:\\Users\\Spinfo\\Desktop\\test_secret17.txt");
        InputStreamReader readerx = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        BufferedReader rr = new BufferedReader(readerx);

        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = rr.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        char[] ccc = sb.toString().toCharArray();
        System.out.println(new String(ccc, 0, ccc.length));

        InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        int len;
        char[] buf = new char[8192];
        char[] current = new char[0];
        while ((len = reader.read(buf)) > 0)
        {
            char[] temp = new char[current.length + len];
            System.arraycopy(current, 0, temp, 0, current.length);
            System.arraycopy(buf, 0, temp, current.length, len);
            current = temp;
        }
        System.out.println(new String(current, 0, current.length));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream input = new FileInputStream(f);
        int len1;
        byte[] buf1 = new byte[8192];
        while ((len1 = input.read(buf1)) > 0)
        {
            out.write(buf1, 0, len1);
        }
        byte[] bytes = out.toByteArray();
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = StandardCharsets.UTF_8.decode(bb);
        System.out.println(cb);


        HttpClientUtil.Config config = new HttpClientUtil.Config();
        config.setKeyStore("D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\spinfossl\\sdk.ks");
        config.setKeyPasswd("C47182A19F40F69B5C022666B72A751CnHXB$f#T");
        config.setKeyStorePasswd("E47CF21F723705F516F5F2F0068001FE@m$5Sq4Q");
        config.setType("JKS");
        config.setSslKeyManager(true);
        Map<String, String> body = new HashMap<>(Collections.singletonMap("jobId", "0560ec07891f472980b78dcb2fa9a29c"));
        body.put("id", "123456789");
        body.put("text", sb.toString());
        Map<String, Object> result = new HttpClientUtil(config).post("https://192.190.116.205/SIMP_DBS_S/event/file/analysis/task/text",
                new HashMap<>(),
                body,
                new TypeReference<Map<String, Object>>()
                {
                });
        System.out.println(result);
    }
}
