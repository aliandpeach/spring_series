package com.yk.httprequest;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * HttpFormDataUtil
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:50
 */

public class HttpFormDataUtil
{
    private static final Logger logger = LoggerFactory.getLogger(HttpFormDataUtil.class);
    /**
     * multipart/form-data 格式发送数据时各个部分分隔符的前缀,必须为 --
     */
    private static final String BOUNDARY_PREFIX = "------";
    private static final String BOUNDARY_SUFFIX = "--";
    /**
     * 回车换行,用于一行的结尾
     */
    private static final String LINE_END = "\r\n";

    public static BaseResponse postFormData(String urlStr,
                                            Map<String, String> filePathMap,
                                            Map<String, String> nameWithContent,
                                            Map<String, Object> headers,
                                            HttpClientUtil.ProxyInfo proxyInfo,
                                            String boundary,
                                            String contentType) throws Exception
    {
        return postFormData(urlStr, filePathMap, nameWithContent, headers, proxyInfo, boundary, contentType, new StringHttpResponseHandler());
    }

    public static <T> T postFormData(String urlStr,
                                     Map<String, String> filePathMap,
                                     Map<String, String> nameWithContent,
                                     Map<String, Object> headers,
                                     HttpClientUtil.ProxyInfo proxyInfo,
                                     String boundary,
                                     String contentType,
                                     HttpResponseHandler<T> httpResponseHandler) throws Exception
    {

        headers = Optional.ofNullable(headers).orElse(new HashMap<>());
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpsURLConnection conn = getHttpsURLConnection(urlStr, headers, proxyInfo);

        //发送参数数据
        try (BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream()))
        {
            if (null != nameWithContent && nameWithContent.size() > 0)
            {
                for (Map.Entry<String, String> _entry : nameWithContent.entrySet())
                    writeSimpleFormField(boundary, out, _entry.getValue(), contentType, _entry.getKey());
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
            throw new RuntimeException("HttpFormDataUtil.postFormData error", e);
        }
        return getHttpResponse(conn, httpResponseHandler);
    }

    private static RequestConfig.Builder REQUEST_CONFIG_BUILDER = RequestConfig.custom()
            .setConnectTimeout(30 * 1000).setConnectionRequestTimeout(30 * 1000)
            .setSocketTimeout(650 * 1000);

    public static BaseResponse postFormDataByHttpClient(String urlStr,
                                                        Map<String, String> filePathMap,
                                                        Map<String, String> nameWithContent,
                                                        Map<String, Object> headers,
                                                        HttpClientUtil.ProxyInfo proxyInfo,
                                                        String boundary,
                                                        String contentType)
    {
        return postFormDataByHttpClient(urlStr, filePathMap, nameWithContent, headers, proxyInfo, boundary, contentType, new StringHttpResponseHandler());
    }

    public static <T> T postFormDataByHttpClient(String urlStr,
                                                 Map<String, String> filePathMap,
                                                 Map<String, String> nameWithContent,
                                                 Map<String, Object> headers,
                                                 HttpClientUtil.ProxyInfo proxyInfo,
                                                 String boundary,
                                                 String contentType,
                                                 HttpResponseHandler<T> httpResponseHandler)
    {
        HttpPost post = new HttpPost(urlStr);
        try
        {
            HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo));
            // 如果new HttpClientUtil不传入proxy信息, 也可以在这里传入requestConfig（前提是requestConfig.setProxy）
//            post.setConfig(REQUEST_CONFIG_BUILDER.setProxy(new HttpHost(proxyInfo.getHostname(), proxyInfo.getPort(), proxyInfo.getScheme())).build());
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

//            Content-Disposition: form-data; name="params"; filename=""
//            这样写可以正常提交到Controller 但是MultipartHttpServletRequest 中总会多出一个params 的文件, 因为报文包含了 filename=""  改为 null 却不能正常提交, 因此使用反射来解决
//            builder.addBinaryBody("params", content.getBytes(StandardCharsets.UTF_8), ContentType.create(contentType), "");

            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("org.apache.http.entity.mime.FormBodyPart");
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Constructor<?> constructor = Arrays.stream(constructors).filter(c -> c.getParameterCount() == 3).findFirst().orElseThrow(() -> new RuntimeException("Constructor<?> null"));
            constructor.setAccessible(true);
            for (Map.Entry<String, String> _entry : nameWithContent.entrySet())
            {
                // 最初版是利用反射构建 FormBodyPart
                Header header = new Header();
                // 重点在这句, 没有设置contentType, FormData中同时传递的json数据无法被controller解析
                header.addField(new MinimalField(MIME.CONTENT_DISPOSITION, "form-data; name=\"" + _entry.getKey() + "\"\r\nContent-Type: " + contentType));
                header.addField(new MinimalField(MIME.CONTENT_TYPE, contentType));
                header.addField(new MinimalField(MIME.CONTENT_TRANSFER_ENC, "binary"));
                FormBodyPart bodyPart = (FormBodyPart) constructor.newInstance(_entry.getKey(),
                        new ByteArrayBody(_entry.getValue().getBytes(StandardCharsets.UTF_8), ContentType.create(contentType), null),
                        header);
//                builder.addPart(bodyPart);
            }

            for (Map.Entry<String, String> _entry : nameWithContent.entrySet())
            {
                // 第二版是利用FormBodyPartBuilder 构建 FormBodyPart
                FormBodyPartBuilder formBodyPartBuilder = FormBodyPartBuilder.create();
                // 重点在这句, 没有设置contentType, FormData中同时传递的json数据无法被controller解析
                formBodyPartBuilder.addField(MIME.CONTENT_DISPOSITION, "form-data; name=\"" + _entry.getKey() + "\"\r\nContent-Type: " + contentType);
                formBodyPartBuilder.addField(MIME.CONTENT_TYPE, contentType);
                formBodyPartBuilder.addField(MIME.CONTENT_TRANSFER_ENC, "binary");
                formBodyPartBuilder.setName(_entry.getKey());
                formBodyPartBuilder.setBody(new ByteArrayBody(_entry.getValue().getBytes(StandardCharsets.UTF_8), ContentType.create(contentType), null));
                FormBodyPart formBodyPart = formBodyPartBuilder.build();
                builder.addPart(formBodyPart);
            }

            if (filePathMap != null && !filePathMap.isEmpty())
            {
                for (Map.Entry<String, String> entry : filePathMap.entrySet())
                {
                    File f = new File(entry.getValue());
                    InputStream input = new FileInputStream(f);
                    builder.addBinaryBody(entry.getKey(), input, ContentType.create("application/octet-stream"), f.getName());
                }
            }
            builder.setCharset(StandardCharsets.UTF_8);
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);


            builder.setBoundary(boundary);

            post.setEntity(builder.build());
            CloseableHttpResponse httpResponse =
                    // // 如果new HttpClientUtil不传入proxy信息, 也可以在这里传入requestConfig（前提是requestConfig.setProxy）
                    util.httpClient.execute(post/*, util.createHttpClientContext(REQUEST_CONFIG_BUILDER.setProxy(new HttpHost(proxyInfo.getHostname(), proxyInfo.getPort(), proxyInfo.getScheme())).build(), null, null)*/);

            return getHttpResponse(httpResponse, httpResponseHandler);
        }
        catch (Exception e)
        {
            throw new RuntimeException("HttpFormDataUtil.postFormDataByHttpClient error ", e);
        }
        finally
        {
            post.releaseConnection();
        }
    }

    /**
     * 获得连接对象
     */
    private static HttpsURLConnection getHttpsURLConnection(String urlStr, Map<String, Object> headers, HttpClientUtil.ProxyInfo proxyInfo) throws
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
        if (null != proxyInfo && proxyInfo.isProxy() && null != proxyInfo.getHostname() && proxyInfo.getPort() > 0)
        {
            InetSocketAddress addr = new InetSocketAddress(proxyInfo.getHostname(), proxyInfo.getPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            conn = (HttpsURLConnection) url.openConnection(proxy);
        }
        else
        {
            conn = (HttpsURLConnection) url.openConnection();
        }

        //设置超时时间
        conn.setConnectTimeout(50 * 1000);
        conn.setReadTimeout(50 * 1000);
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

    public static <T> T getHttpResponse(HttpURLConnection conn, @Nullable HttpResponseHandler<T> httpResponseHandler) throws IOException
    {
        if (null == httpResponseHandler)
        {
            throw new RuntimeException("get http response error");
        }
        int _code = conn.getResponseCode();
        if (_code >= 200 && _code <= 300)
            return httpResponseHandler.handleHttpResponse(new HttpResponse<>(_code, conn.getInputStream(), conn.getHeaderFields()));

        String _message = IOUtils.toString(conn.getErrorStream());
        throw new RuntimeException(_message);
    }

    public static <T> T getHttpResponse(CloseableHttpResponse response, @Nullable HttpResponseHandler<T> httpResponseHandler) throws IOException
    {
        if (null == httpResponseHandler)
        {
            throw new RuntimeException("get http response error");
        }
        int _code = response.getStatusLine().getStatusCode();
        if (_code >= 200 && _code <= 300)
            return httpResponseHandler.handleHttpResponse(new HttpResponse<>(response.getStatusLine().getStatusCode(), response.getEntity().getContent(), null));
        throw new RuntimeException(EntityUtils.toString(response.getEntity()));
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

            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

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
    private static void writeSimpleFormField(String boundary, BufferedOutputStream out, String content, String contentType, String contentName) throws
            IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(BOUNDARY_PREFIX).append(boundary).append(LINE_END);
        sb.append(String.format("Content-Disposition: form-data; name=\"%s\"", contentName)).append(LINE_END);
        sb.append("Content-Type: ").append(contentType);
        sb.append(LINE_END);
        sb.append(LINE_END);
        sb.append(content).append(LINE_END);
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static BaseResponse postBytes(String urlStr,
                                         byte[] content,
                                         HttpClientUtil.ProxyInfo proxyInfo) throws Exception
    {
        return postBytes(urlStr, content, proxyInfo, new StringHttpResponseHandler());
    }

    /**
     * 发送文本内容 发送 byte[] 到controller，controller参数为 @RequestBody byte[] bytes
     *
     * @throws IOException
     */
    public static <T> T postBytes(String urlStr,
                                  byte[] content,
                                  HttpClientUtil.ProxyInfo proxyInfo,
                                  HttpResponseHandler<T> httpResponseHandler) throws Exception
    {
        HttpsURLConnection conn = getHttpsURLConnection(urlStr, new HashMap<>(), proxyInfo);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);

        try (BufferedOutputStream writer = new BufferedOutputStream(conn.getOutputStream());
             ByteArrayInputStream reader = new ByteArrayInputStream(content))
        {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, len);
            }

            return getHttpResponse(conn, httpResponseHandler);
        }
        catch (Exception e)
        {
            throw new RuntimeException("HttpFormDataUtil.postBytes error", e);
        }
    }

    public interface HttpResponseHandler<T>
    {
        T handleHttpResponse(HttpResponse<T> response) throws IOException;
    }

    public static class StringHttpResponseHandler implements HttpResponseHandler<BaseResponse>
    {
        @Override
        public BaseResponse handleHttpResponse(HttpResponse<BaseResponse> response) throws IOException
        {
            return new BaseResponse(response.getCode(), IOUtils.toString(response.getContent(), StandardCharsets.UTF_8));
        }
    }

    @Data
    public static class HttpResponse<T>
    {
        private int code;

        private InputStream content;

        private Map<String, List<String>> headers;

        public HttpResponse(int status, InputStream content, Map<String, List<String>> headers)
        {
            this.code = status;
            this.content = content;
            this.headers = headers;
        }

        public String toString()
        {
            return "[ code = " + code +
                    " , content = " + content + " ]";
        }
    }

    @Data
    public static class BaseResponse
    {
        private int code;

        private String content;

        private int number = 0;

        public BaseResponse(int code, String content)
        {
            this.code = code;
            this.content = content;
        }
    }
}