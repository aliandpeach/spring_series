package com.http;

import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/26 15:43:12
 */
public class HttpConnectionTest
{
    @Test
    public void testConnection() throws IOException
    {
        URL url = new URL(null,
                "http://zjjcmspublic.oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn/jcms_files/jcms1/web1825/site/attach/0/9d45d21c4950410db2eea73d097334c6.pdf",
                new sun.net.www.protocol.https.Handler());
        InetSocketAddress addr = new InetSocketAddress("192.190.10.101", 3128);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(proxy);

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        InputStream in = conn.getInputStream();
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        int status = conn.getResponseCode();
        System.out.println("");
    }
}
