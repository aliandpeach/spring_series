package com.http;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.yk.httprequest.JSONUtil;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientTest
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:50
 */

public class HttpClientTest
{
    /**
     * multipart/form-data 格式发送数据时各个部分分隔符的前缀,必须为 --
     */
    private static final String BOUNDARY_PREFIX = "------";
    private static final String BOUNDARY_SUFFIX = "--";
    /**
     * 回车换行,用于一行的结尾
     */
    private static final String LINE_END = "\r\n";
    
    public static HttpResponse postFormData(String urlStr, Map<String, String> filePathMap, String json, Map<String, Object> headers, boolean proxy) throws
            Exception
    {
        HttpResponse response;
        HttpsURLConnection conn = getHttpsURLConnection(urlStr, headers, proxy);
        
        String boundary = "WebKitFormBoundary2ikDa4yTuM4d47aa";
        
        //发送参数数据
        try (BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream()))
        {
            if (null != json)
            {
                writeSimpleFormField(boundary, out, json);
            }
            
            //发送文件类型参数
            if (filePathMap != null && !filePathMap.isEmpty())
            {
                for (Map.Entry<String, String> filePath : filePathMap.entrySet())
                {
                    writeFile(filePath.getKey(), filePath.getValue(), boundary, out);
                }
            }
    
            //写结尾的分隔符--${boundary}--,然后回车换行
            String endStr = BOUNDARY_PREFIX + boundary + BOUNDARY_SUFFIX + LINE_END;
            out.write(endStr.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            response = new HttpResponse(500, e.getMessage());
            return response;
        }
        
        return getHttpResponse(conn);
    }
    
    /**
     * 获得连接对象
     *
     * @param urlStr
     * @param headers
     * @return
     * @throws IOException
     */
    private static HttpsURLConnection getHttpsURLConnection(String urlStr, Map<String, Object> headers, boolean proxyBoolean) throws
            Exception
    {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            @Override
            public boolean verify(String s, SSLSession sslSession)
            {
                return true;
            }
        });
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{new X509TrustManager()
        {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
            {
            
            }
            
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
            {
            
            }
            
            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }
        }}, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        URL url = new URL(null, urlStr, new sun.net.www.protocol.https.Handler());
        HttpsURLConnection conn = null;
        if (proxyBoolean)
        {
            InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            conn = (HttpsURLConnection) url.openConnection(proxy);
        }
        else
        {
            conn = (HttpsURLConnection) url.openConnection();
        }
        
        //设置超时时间
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);
        //允许输入流
        conn.setDoInput(true);
        //允许输出流
        conn.setDoOutput(true);
        //不允许使用缓存
        conn.setUseCaches(false);
        //请求方式
        conn.setRequestMethod("POST");
        //设置编码 utf-8
        conn.setRequestProperty("Charset", "UTF-8");
        //设置为长连接
        conn.setRequestProperty("connection", "keep-alive");
        
        //设置其他自定义 headers
        if (headers != null && !headers.isEmpty())
        {
            for (Map.Entry<String, Object> header : headers.entrySet())
            {
                conn.setRequestProperty(header.getKey(), header.getValue().toString());
            }
        }
        
        return conn;
    }
    
    private static HttpResponse getHttpResponse(HttpURLConnection conn)
    {
        HttpResponse response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")))
        {
            int responseCode = conn.getResponseCode();
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                responseContent.append(line);
            }
            response = new HttpResponse(responseCode, responseContent.toString());
        }
        catch (Exception e)
        {
            response = new HttpResponse(500, e.getMessage());
        }
        return response;
    }
    
    /**
     * 写文件类型的表单参数
     *
     * @param paramName 参数名
     * @param filePath  文件路径
     * @param boundary  分隔符
     * @param out
     * @throws IOException
     */
    private static void writeFile(String paramName, String filePath, String boundary,
                                  BufferedOutputStream out)
    {
        try (InputStream inputStream = (new FileInputStream(filePath)))
        {
            StringBuilder sb = new StringBuilder();
            sb.append(BOUNDARY_PREFIX).append(boundary).append(LINE_END);
            /**
             * Content-Disposition: form-data; name="参数名"; filename="文件名"
             * Content-Type: application/octet-stream
             */
            String fileName = new File(filePath).getName();
            sb.append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", paramName, fileName)).append(LINE_END);
            sb.append("Content-Type: application/octet-stream" + LINE_END + LINE_END);
            
            out.write(sb.toString().getBytes("UTF-8"));
            
            int bufSize = 8 * 1024;
            byte[] buffer = new byte[bufSize];
            int len;
            while (-1 != (len = inputStream.read(buffer, 0, bufSize)))
            {
                out.write(buffer, 0, len);
            }
            out.write(LINE_END.getBytes());
        }
        catch (Exception e)
        {
        }
    }
    
    /**
     * 写普通的表单参数
     *
     * @param boundary 分隔符
     * @param out
     * @throws IOException
     */
    private static void writeSimpleFormField(String boundary, BufferedOutputStream out, String json) throws
            IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BOUNDARY_PREFIX).append(boundary).append(LINE_END);
        sb.append(String.format("Content-Disposition: form-data; name=\"%s\"", "params")).append(LINE_END);
        sb.append("Content-Type: application/json");
        sb.append(LINE_END);
        sb.append(LINE_END);
        sb.append(json).append(LINE_END);
        out.write(sb.toString().getBytes("UTF-8"));
    }
    
    /**
     * Content-Type: multipart/form-data; boundary=----WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * <p>
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files1"; filename="1.txt"
     * Content-Type: text/plain
     * <p>
     * 111111
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files2"; filename="2.txt"
     * Content-Type: text/plain
     * <p>
     * 222222
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="params"; filename="blob"
     * Content-Type: application/json
     * <p>
     * {"id":"1619604419314","fileName":"name1"}
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi--
     */
    @Test
    public void sendFormData() throws Exception
    {
        String url = "https://192.190.10.122:4433/SIMP_DBS_S/event/upload/file/form";
//        url = "https://192.190.10.122:21112/import/upload/multiple/json";

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "1111");
        jsonObject.put("fileName", "2.txt");
        jsonArray.add(jsonObject);
        
        Map<String, String> filePathMap = new HashMap<>();
        String paramName = "1111";
        String filePath = "D:\\opt\\up\\2.txt";
        filePathMap.put(paramName, filePath);
        
        String boundary = "WebKitFormBoundary2ikDa4yTuM4d47aa";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpResponse response = postFormData(url, filePathMap, jsonArray.toJSONString(0), headers, false);
        System.out.println(response);
        
    }
    
    /**
     * 发送文本内容
     *
     * @param urlStr
     * @param filePath
     * @return
     * @throws IOException
     */
    public static HttpResponse postText(String urlStr, String filePath) throws IOException
    {
        HttpResponse response = null;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
             BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8.name())))
        {
            String line;
            while ((line = fileReader.readLine()) != null)
            {
                writer.write(line);
            }
            
        }
        catch (Exception e)
        {
            return response;
        }
        
        return getHttpResponse(conn);
    }
    
    public static class HttpResponse
    {
        private int code;
        
        private String content;
        
        public HttpResponse(int status, String content)
        {
            this.code = status;
            this.content = content;
        }
        
        public int getCode()
        {
            return code;
        }
        
        public void setCode(int code)
        {
            this.code = code;
        }
        
        public String getContent()
        {
            return content;
        }
        
        public void setContent(String content)
        {
            this.content = content;
        }
        
        public String toString()
        {
            return new StringBuilder("[ code = ").append(code)
                    .append(" , content = ").append(content).append(" ]").toString();
        }
    }
}