package com.http;

import cn.hutool.core.util.HexUtil;
import com.crypto.sm.SM2Test;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.JSONUtil;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
        config.setKeyStore("D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk.ks");
        config.setKeyPasswd("C47182A19F40F69B5C022666B72A751CnHXB$f#T");
        config.setKeyStorePasswd("E47CF21F723705F516F5F2F0068001FE@m$5Sq4Q");
        config.setType("JKS");
        config.setSslKeyManager(true);
        Map<String, String> param = new HashMap<>(Collections.singletonMap("jobId", "jobId"));
        param.put("id", "id");
        param.put("name", "name");
        Map<String, String> result = new HttpClientUtil(config).get("https://192.168.31.205:443/base/event/file/analysis/analyze",
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
    public void testGetQuery() throws Exception
    {
        HttpClientUtil.Config config = new HttpClientUtil.Config();
//        config.setProxyInfo(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http"));
        config.setSocketTimeout(120000);
        Map<String, String> body = new HashMap<>(Collections.singletonMap("jobId", "8303261fd4eb44249ab76a69a17ff46d"));
        body.put("taskId", "29572e44-25e5-401d-9e2f-217dd90652d0");

        Map<String, String> headers = new HashMap<>();

        SM2Test sm2 = new SM2Test();
        String time = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String str = "SS0EA21120003:" + time;
        String encrypt = sm2.encode(str, "04B917C2246315CEE1BB413E44FD0093373C1E04263E473954BE36CAA470EE3651FCFF0DCCEA3173646BC3C779627FF7ADA0E66495A15D317F253E37F0070269E4");
        System.out.println(encrypt);
        headers.put("Authorization", encrypt);

        Map<String, Object> model = new HttpClientUtil(config).get("https://192.168.31.251:443/base/event/doc/query",
                headers,
                body,
                new TypeReference<Map<String, Object>>()
                {
                }, 1);
        System.out.println(model);
    }

    @Test
    public void testPostUpload() throws Exception
    {
        HttpClientUtil.Config config = new HttpClientUtil.Config();
//        config.setProxyInfo(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http"));
        config.setSocketTimeout(120000);
        Map<String, String> body = new HashMap<>(Collections.singletonMap("jobId", "8303261fd4eb44249ab76a69a17ff46d"));
        body.put("url", "http://zjjcmspublic.oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn/jcms_files/jcms1/web3431/site/old/webmagic/eWebEditor/uploadfile/20140723085535625.jpg");

        Map<String, String> headers = new HashMap<>();

        SM2Test sm2 = new SM2Test();
        String time = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String str = "SS0EA21120003:" + time;
        String encrypt = sm2.encode(str, "04B917C2246315CEE1BB413E44FD0093373C1E04263E473954BE36CAA470EE3651FCFF0DCCEA3173646BC3C779627FF7ADA0E66495A15D317F253E37F0070269E4");
        System.out.println(encrypt);
        headers.put("Authorization", encrypt);

        Map<String, Object> model = new HttpClientUtil(config).post("https://192.168.31.251:443/base/event/doc/upload",
                headers,
                body,
                new TypeReference<Map<String, Object>>()
                {
                });
        System.out.println(model);
    }

    @Test
    public void testPostText() throws Exception
    {
        File f = new File("F:\\Downloads\\B6ABBDD8-670A-4682-8F0B-395D0C0966E3.txt");
        InputStreamReader readerx = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        BufferedReader rr = new BufferedReader(readerx);

        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = rr.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        char[] ccc = sb.toString().toCharArray();
//        System.out.println(new String(ccc, 0, ccc.length));

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
//        System.out.println(new String(current, 0, current.length));

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
//        System.out.println(cb);

        HttpClientUtil.Config config = new HttpClientUtil.Config();
//        config.setKeyStore("D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk.ks");
//        config.setKeyPasswd("C47182A19F40F69B5C022666B72A751CnHXB$f#T");
//        config.setKeyStorePasswd("E47CF21F723705F516F5F2F0068001FE@m$5Sq4Q");
//        config.setType("JKS");
//        config.setSslKeyManager(true);
//        config.setProxyInfo(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http"));
        config.setSocketTimeout(120000);
        Map<String, String> body = new HashMap<>(Collections.singletonMap("jobId", "8303261fd4eb44249ab76a69a17ff46d"));
        body.put("id", "123456789");
        body.put("text", sb.toString());

        Map<String, String> headers = new HashMap<>();

        SM2Test sm2 = new SM2Test();
        String time = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String str = "SS0EA21120003:" + time;
        String encrypt = sm2.encode(str, "04B917C2246315CEE1BB413E44FD0093373C1E04263E473954BE36CAA470EE3651FCFF0DCCEA3173646BC3C779627FF7ADA0E66495A15D317F253E37F0070269E4");
        System.out.println(encrypt);
        headers.put("Authorization", encrypt);

        long start = System.currentTimeMillis();
        Model model = new HttpClientUtil(config).post("https://192.168.31.251/base/event/doc/text",
                headers,
                body,
                new TypeReference<Model>()
                {
                });
        System.out.println(Optional.ofNullable(model.getResult()).orElse(new ArrayList<>()).size());
        System.out.println(JSONUtil.toJson(model));
        System.out.println(System.currentTimeMillis() - start);
    }

    private static class Model
    {
        private int code;
        private String level;
        private String state;
        private String message;
        private String taskId;
        private String secretRelatedLevel;

        private List<Info> result;

        public int getCode()
        {
            return code;
        }

        public void setCode(int code)
        {
            this.code = code;
        }

        public String getLevel()
        {
            return level;
        }

        public void setLevel(String level)
        {
            this.level = level;
        }

        public String getState()
        {
            return state;
        }

        public void setState(String state)
        {
            this.state = state;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getTaskId()
        {
            return taskId;
        }

        public void setTaskId(String taskId)
        {
            this.taskId = taskId;
        }

        public String getSecretRelatedLevel()
        {
            return secretRelatedLevel;
        }

        public void setSecretRelatedLevel(String secretRelatedLevel)
        {
            this.secretRelatedLevel = secretRelatedLevel;
        }

        public List<Info> getResult()
        {
            return result;
        }

        public void setResult(List<Info> result)
        {
            this.result = result;
        }
    }

    private static class Info
    {
        private List<Integer> pos;
        private String matchContent;
        private String source;
        private String secretRate;
        private String externalBreachContent;
        private String secretRelatedLevel;

        public List<Integer> getPos()
        {
            return pos;
        }

        public void setPos(List<Integer> pos)
        {
            this.pos = pos;
        }

        public String getMatchContent()
        {
            return matchContent;
        }

        public void setMatchContent(String matchContent)
        {
            this.matchContent = matchContent;
        }

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String getSecretRate()
        {
            return secretRate;
        }

        public void setSecretRate(String secretRate)
        {
            this.secretRate = secretRate;
        }

        public String getExternalBreachContent()
        {
            return externalBreachContent;
        }

        public void setExternalBreachContent(String externalBreachContent)
        {
            this.externalBreachContent = externalBreachContent;
        }

        public String getSecretRelatedLevel()
        {
            return secretRelatedLevel;
        }

        public void setSecretRelatedLevel(String secretRelatedLevel)
        {
            this.secretRelatedLevel = secretRelatedLevel;
        }
    }
}
