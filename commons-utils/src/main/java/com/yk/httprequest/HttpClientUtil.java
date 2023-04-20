package com.yk.httprequest;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpClientUtil
{
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    //setConnectTimeout：设置连接超时时间，单位毫秒。
    //setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
    //setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用， 下载一个大文件的时候，只要有持续响应，文件流就不会中断直到结束传输(除非中间有网络问题导致的超过SocketTimeout的时间)。

    public final CloseableHttpClient httpClient;

    public final RequestConfig requestConfig;

    public HttpClientUtil() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        this(new Config());
    }

    public HttpClientUtil(Config config) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        if (null == config)
        {
            config = new Config();
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setSocketTimeout(config.getSocketTimeout());
        requestConfigBuilder.setConnectTimeout(config.getConnectTimeout());
        requestConfigBuilder.setConnectionRequestTimeout(config.getConnectionRequestTimeout());

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial((chain, authType) -> true);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        config.configError();
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("SunX509");
        if (config.isSslKeyManager())
        {
            KeyStore key = KeyStore.getInstance(config.getType());
            key.load(new FileInputStream(config.getKeyStore()), config.getKeyStorePasswd().toCharArray());
            keyFactory.init(key, config.getKeyPasswd().toCharArray());
        }
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
        if (config.isSslTrustManager())
        {
            KeyStore trust = KeyStore.getInstance(config.getType());
            trust.load(new FileInputStream(config.getTrustStore()), config.getTrustStorePasswd().toCharArray());
            trustFactory.init(trust);
        }
        sslContext.init(config.isSslKeyManager() ? keyFactory.getKeyManagers() : null,
                config.isSslTrustManager() ? trustFactory.getTrustManagers() : new TrustManager[]{new NullX509TrustManager()},
                new SecureRandom());

        // NoopHostnameVerifier | DefaultHostnameVerifier
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);
        Registry<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();

        //连接池管理类
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder);
        poolingHttpClientConnectionManager.setMaxTotal(3000);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(3000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setConnectionManagerShared(true)/*.setDefaultRequestConfig(defaultRequestConfigBuilder.build())*/;

        // 服务器认证: Basic, Digest and NTLM. 主要用于soap-webservice的登录验证
        if (null != config.getCredentials() && null != config.getCredentials().getUsername() && null != config.getCredentials().getPasswd())
        {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(config.getCredentials().getUsername(), config.getCredentials().getPasswd());
            NTCredentials ntCredentials = new NTCredentials(config.getCredentials().getUsername(), config.getCredentials().getPasswd(), "", "");
            credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY), ntCredentials);

            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        // 服务器代理
        if (null != config.getProxyInfo() && config.getProxyInfo().isProxy())
        {
            HttpHost httpProxy = new HttpHost(config.getProxyInfo().getHostname(), config.getProxyInfo().getPort(), config.getProxyInfo().getScheme());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpProxy);

            // 这里设置的proxy不受requestConfig.setProxy影响, setProxy  setRoutePlanner最终都是为了生成 DefaultProxyRoutePlanner, 二者使用一个即可
            httpClientBuilder.setRoutePlanner(routePlanner).setProxy(httpProxy);

            // RequestConfig.Builder 的 setProxy() 也一样最后都是用于生成 DefaultProxyRoutePlanner
            // (RequestConfig的方式可以灵活的使 CloseableHttpClient 发送请求的时候使用或者不使用代理)

            // 注意：在InternalHttpClient类, 优先使用的是HttpPost/HttpGet等设置的RequestConfig
            // 如果没有, 才会使用HttpClientContext中设置的 RequestConfig
            // 这里配置的httpProxy只是为了组装requestConfig, 想要proxy生效, 在execute前,调用httpPost.setConfig或者HttpClientContext.setRequestConfig或者httpClientBuilder.setDefaultRequestConfig(requestConfig)
            requestConfigBuilder.setProxy(httpProxy);
        }

        requestConfig = requestConfigBuilder.build();
        // 这里的requestConfig中设置的proxy 受到httpPost.setConfig或者HttpClientContext.setRequestConfig的影响
        httpClient = httpClientBuilder.setDefaultRequestConfig(requestConfig).build();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                logger.info("closing http client");
                httpClient.close();
                logger.info("http client closed");
            }
            catch (IOException e)
            {
                logger.error(e.getMessage(), e);
            }
        }));
    }

    public HttpClientContext createHttpClientContext(RequestConfig requestConfig, HttpHost target, CredentialsProvider credentialsProvider)
    {
        HttpClientContext httpContext = HttpClientContext.create();
        if (null != requestConfig)
            httpContext.setRequestConfig(requestConfig);
        if (null != target)
        {
            AuthCache authCache = new BasicAuthCache();
            authCache.put(target, new BasicScheme());
            httpContext.setAuthCache(authCache);
        }
        if (null != credentialsProvider)
            httpContext.setCredentialsProvider(credentialsProvider);
        return httpContext;
    }

    /**
     * 发送一般的json格式请求, 返回json格式数据, 通过Jackson 转换为对象
     */
    public <T> T post(String url,
                      Map<String, String> headers,
                      Map<String, Object> body,
                      final TypeReference<T> typeReference)
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = new HttpPost(url);
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(cn.hutool.json.JSONUtil.toJsonStr(body));
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            httpPost.setConfig(requestConfig);
            return httpClient.execute(httpPost, new JsonResponseHandler<T>(typeReference));
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
        finally
        {
            if (null != httpPost)
                httpPost.releaseConnection();
        }
    }

    public void downloadPost(String url,
                             Map<String, String> headers,
                             Map<String, String> body,
                             HttpResponseHandler httpResponseHandler)
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = new HttpPost(url);
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(cn.hutool.json.JSONUtil.toJsonStr(body));
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            httpPost.setConfig(requestConfig);
            httpResponseHandler.handleHttpResponse(httpClient.execute(httpPost));
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
        finally
        {
            if (null != httpPost)
                httpPost.releaseConnection();
        }
    }

    /**
     * post请求编码格式:application/x-www-form-urlencoded, 请求体为格式为:
     *
     *
     * POST /upload/xxx HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 469662a0-65ec-4329-a7e0-3743c6122da0
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: application/x-www-form-urlencoded
     * Content-Length: 18
     *
     * _key=123&_name=235
     *
     */
    public <T> T postFormUrlencoded(String url,
                                    Map<String, String> headers,
                                    Map<String, String> body,
                                    final TypeReference<T> typeReference)
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = new HttpPost(url);
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setParameters(Optional.ofNullable(body).orElse(new HashMap<>()).entrySet().stream().map(t -> new BasicNameValuePair(t.getKey(), t.getValue())).collect(Collectors.toList()));
            builder.setContentType(ContentType.APPLICATION_FORM_URLENCODED);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            httpPost.setConfig(requestConfig);
            return httpClient.execute(httpPost, new JsonResponseHandler<T>(typeReference));
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
        finally
        {
            if (null != httpPost)
                httpPost.releaseConnection();
        }
    }

    /**
     * 发送POST请求请求体为 multipart/form-data, 该方法能上传文件以及其他参数, 如果需要传递application/json等特殊其他参数, 则使用HttpFormDataUtil.postFormData
     */
    public <T> T postFormData(String url,
                              Map<String, String> headers,
                              Map<String, String> body,
                              String localFile,
                              final TypeReference<T> typeReference)
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = new HttpPost(url);
            initHeader(httpPost, headers);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), StandardCharsets.UTF_8);
            Optional.ofNullable(body).orElse(new HashMap<>()).forEach((key, value) -> multipartEntityBuilder.addPart(key, new StringBody(value, contentType)));
            if (StringUtils.isNotBlank(localFile) && new File(localFile).exists())
                multipartEntityBuilder.addBinaryBody(new File(localFile).getName(), new File(localFile));
            httpPost.setEntity(multipartEntityBuilder.build());

            httpPost.setConfig(requestConfig);
            return httpClient.execute(httpPost, new JsonResponseHandler<T>(typeReference));
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
        finally
        {
            if (null != httpPost)
                httpPost.releaseConnection();
        }
    }

    public String postXml(String url,
                          Map<String, String> headers,
                          String xml)
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = new HttpPost(url);
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(xml);
            builder.setContentType(ContentType.APPLICATION_XML);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            httpPost.setConfig(requestConfig);
            return EntityUtils.toString(httpClient.execute(httpPost).getEntity());
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
        finally
        {
            if (null != httpPost)
                httpPost.releaseConnection();
        }
    }

    /**
     * 返回json格式数据, 通过Jackson 转换为对象
     */
    public <T> T get(String url,
                     Map<String, String> headers,
                     Map<String, String> params,
                     TypeReference<T> tTypeReference,
                     int times)
    {
        long start = System.currentTimeMillis();
        HttpGet httpGet = null;
        try
        {
            url = initUrlParams(url, params);
            httpGet = new HttpGet(url);
            initHeader(httpGet, headers);

            httpGet.setConfig(requestConfig);
            return httpClient.execute(httpGet, new JsonResponseHandler<>(tTypeReference));
        }
        catch (IOException e)
        {
            long end = System.currentTimeMillis();
            logger.error("IOException time : " + (end - start), e);
            if (times > 0)
            {
                return get(url, headers, params, tTypeReference, --times);
            }
            throw new RuntimeException("T get error", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("T get error", e);
        }
        finally
        {
            if (null != httpGet)
            {
                httpGet.releaseConnection();
            }
        }
    }

    public void downloadGet(String url,
                            Map<String, String> headers,
                            Map<String, String> params, HttpResponseHandler httpResponseHandler)
    {

        HttpGet httpGet = null;
        try
        {
            url = initUrlParams(url, params);
            httpGet = new HttpGet(url);
            initHeader(httpGet, headers);

            httpGet.setConfig(requestConfig);
            httpResponseHandler.handleHttpResponse(httpClient.execute(httpGet));
        }
        catch (Exception e)
        {
            throw new RuntimeException("downloadGet error", e);
        }
        finally
        {
            if (null != httpGet)
                httpGet.releaseConnection();
        }
    }

    public String getString(String url, Map<String, String> headers, Map<String, String> params)
    {
        HttpGet httpGet = null;
        try
        {
            url = initUrlParams(url, params);
            httpGet = new HttpGet(url);
            initHeader(httpGet, headers);

            httpGet.setConfig(requestConfig);
            return EntityUtils.toString(httpClient.execute(httpGet).getEntity());
        }
        catch (IOException e)
        {
            throw new RuntimeException("getString error", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("getString Exception error", e);
        }
        finally
        {
            if (null != httpGet)
                httpGet.releaseConnection();
        }
    }

    private static String initUrlParams(String url, Map<String, String> params)
    {
        if (params == null)
        {
            return url;
        }
        URIBuilder builder = null;
        try
        {
            builder = new URIBuilder(url);
            URIBuilder finalBuilder = builder;
            params.forEach(finalBuilder::setParameter);
            url = builder.build().toString();
        }
        catch (URISyntaxException e)
        {
            logger.error("init builder url params error", e);
        }
        return url;
    }

    private static void initHeader(HttpRequestBase httpRequestBase, Map<String, String> headers)
    {
        if (httpRequestBase == null)
        {
            return;
        }
        if (headers == null)
        {
            return;
        }
        httpRequestBase.addHeader("ContentType", "application/json");
        headers.forEach(httpRequestBase::addHeader);
    }

    static class XmlResponseHandler implements ResponseHandler<String>
    {
        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
        {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200 || status >= 300)
            {
                throw new IOException("response status is not correct");
            }
            HttpEntity httpEntity = response.getEntity();
            if (null == httpEntity)
            {
                throw new IOException("httpEntity is null");
            }
            return EntityUtils.toString(httpEntity);
        }
    }

    static class JsonResponseHandler<T> implements ResponseHandler<T>
    {

        private final TypeReference<T> typeReference;

        public JsonResponseHandler(TypeReference<T> typeReference)
        {
            this.typeReference = typeReference;
        }

        @Override
        public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException
        {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200 || status >= 300)
            {
                throw new IOException("response status is not correct");
            }
            HttpEntity httpEntity = response.getEntity();
            if (null == httpEntity)
            {
                throw new IOException("httpEntity is null");
            }
            String string = EntityUtils.toString(httpEntity);
            return JSONUtil.fromJson(string, typeReference);
        }
    }

    private static class NullX509TrustManager implements X509TrustManager
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
    }

    @Data
    public static class ProxyInfo
    {
        private boolean proxy;

        private String hostname;

        private int port;

        private String scheme = "http";

        public ProxyInfo(boolean proxy, String hostname, int port, String scheme)
        {
            this.proxy = proxy;
            this.hostname = hostname;
            this.port = port;
            this.scheme = scheme;
        }
    }

    @Data
    public static class Config
    {
        private String keyStore;
        private String keyStorePasswd;
        private String keyPasswd;
        private String type;

        private String trustStore;
        private String trustStorePasswd;

        private boolean sslKeyManager;
        private boolean sslTrustManager;

        private int connectTimeout = 20000; //setConnectTimeout：设置连接超时时间，单位毫秒。

        private int connectionRequestTimeout = -1;//setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。

        private int socketTimeout = 20000; //setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用， 下载一个大文件的时候，只要有持续响应，文件流就不会中断直到结束传输(除非中间有网络问题导致的超过SocketTimeout的时间)。

        private ProxyInfo proxyInfo;

        private Credentials credentials;

        public Config ofProxy(ProxyInfo proxyInfo)
        {
            this.proxyInfo = proxyInfo;
            return this;
        }

        public Config ofCredentials(Credentials credentials)
        {
            this.credentials = credentials;
            return this;
        }

        public void configError()
        {
            if (this.isSslKeyManager() && (isEmpty(keyStore) || isEmpty(keyStorePasswd) || isEmpty(keyPasswd) || isEmpty(type)))
            {
                throw new RuntimeException("ssl key store is null");
            }
            if (this.isSslTrustManager() && (isEmpty(trustStore) || isEmpty(trustStorePasswd) || isEmpty(type)))
            {
                throw new RuntimeException("ssl trust store is null");
            }
        }
    }

    @Data
    public static class Credentials
    {
        private String username;

        private String passwd;

        private String scheme = "http";

        private Authenticator auth;

        public Credentials(String username, String passwd, String scheme)
        {
            this.username = username;
            this.passwd = passwd;
            this.scheme = scheme;
            auth = new Authenticator()
            {
                public PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username, passwd.toCharArray());
                }
            };
//            Authenticator.setDefault(auth);
        }
    }

    private static boolean isEmpty(String str)
    {
        return null == str || str.trim().length() == 0;
    }

    public static class ByteArrayRequestEntity extends BasicHttpEntity
    {
        // request body 写入ByteArrayOutputStream 缓存
        private ByteArrayOutputStream os = null;

        public ByteArrayRequestEntity(OutputStream os)
        {
            super();
            this.os = (ByteArrayOutputStream) os;
        }

        @Override
        public long getContentLength()
        {
            return os.size();
        }

        @Override
        public Header getContentType()
        {
            return new BasicHeader("Content-Type", "text/xml; charset=utf-8");
        }

        @Override
        public boolean isRepeatable()
        {
            return true;// important
        }

        @Override
        public void writeTo(OutputStream out) throws IOException
        {
            // ByteArrayOutputStream 缓存的request body写入 connection.getOutputStream();
            os.writeTo(out);
        }

        @Override
        public boolean isStreaming()
        {
            return false;
        }
    }

    public interface HttpResponseHandler
    {
        void handleHttpResponse(HttpResponse response) throws IOException;
    }
}