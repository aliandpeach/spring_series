package com.yk.httprequest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpFormDataUtil
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:50
 */

public class HttpFormDataUtil
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

    public static HttpResponse postFormData(String urlStr,
                                            Map<String, String> filePathMap,
                                            String content,
                                            Map<String, Object> headers,
                                            boolean proxy,
                                            String boundary,
                                            String contentType) throws
            Exception
    {
        HttpResponse response;
        HttpsURLConnection conn = getHttpsURLConnection(urlStr, headers, proxy);

        //发送参数数据
        try (BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream()))
        {
            if (null != content)
            {
                writeSimpleFormField(boundary, out, content, contentType);
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
            response = new HttpResponse(400, e.getMessage());
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
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
            response = new HttpResponse(400, e.getMessage());
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
    private static void writeSimpleFormField(String boundary, BufferedOutputStream out, String content, String contentType) throws
            IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BOUNDARY_PREFIX).append(boundary).append(LINE_END);
        sb.append(String.format("Content-Disposition: form-data; name=\"%s\"", "params")).append(LINE_END);
        sb.append(contentType);
        sb.append(LINE_END);
        sb.append(LINE_END);
        sb.append(content).append(LINE_END);
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 发送文本内容 发送 byte[] 到controller，controller参数为 @RequestBody byte[] bytes
     *
     * @param urlStr
     * @param content
     * @return
     * @throws IOException
     */
    public static HttpResponse postText(String urlStr, String content) throws Exception
    {
        HttpsURLConnection conn = getHttpsURLConnection(urlStr, new HashMap<>(), true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
             StringReader reader = new StringReader(content))
        {
            char[] buffer = new char[4096];
            int len;
            while ((len = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, len);
            }
        }
        catch (Exception e)
        {
            return new HttpResponse(400, e.getMessage());
        }

        return getHttpResponse(conn);
    }

    public static class HttpResponse
    {
        private int code;

        private String content;

        private int number;

        public int getNumber()
        {
            return number;
        }

        public void setNumber(int number)
        {
            this.number = number;
        }

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
            return "[ code = " + code +
                    " , content = " + content + " ]";
        }
    }
}