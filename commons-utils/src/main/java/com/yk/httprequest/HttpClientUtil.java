package com.yk.httprequest;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClientUtil
{
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    //setConnectTimeout：设置连接超时时间，单位毫秒。
    //setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
    //setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(22000).setConnectionRequestTimeout(12000)
            .setSocketTimeout(24000).build();

    public static final ThreadLocal<Config> CONFIG_THREAD_LOCAL = new ThreadLocal<>();
    public static final ThreadLocal<RequestConfig> REQUEST_CONFIG_THREAD_LOCAL = new ThreadLocal<>();

    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    
    public static CloseableHttpClient getClient(Config config) throws GeneralSecurityException, IOException
    {
        if (null == config)
        {
            throw new RuntimeException("config must not be null");
        }
        if (null == httpClient)
        {
            synchronized (HttpClientUtil.class)
            {
                if (null == httpClient)
                {
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
                            .setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig);

                    // 服务器认证: Basic, Digest and NTLM. 主要用于soap-webservice的登录验证
                    if (null != config.getCredentials() && null != config.getCredentials().getUsername() && null != config.getCredentials().getPasswd())
                    {
                        CredentialsProvider provider = new BasicCredentialsProvider();
                        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(config.getCredentials().getUsername(), config.getCredentials().getPasswd());
                        NTCredentials webServiceCredentials = new NTCredentials(config.getCredentials().getUsername(), config.getCredentials().getPasswd(), "", "");
                        provider.setCredentials(new AuthScope(AuthScope.ANY), webServiceCredentials);
                        httpClientBuilder.setDefaultCredentialsProvider(provider);
                    }

                    // 设置代理
                    if (null != config.getProxyInfo() && config.getProxyInfo().isProxy())
                    {
                        HttpHost httpProxy = new HttpHost(config.getProxyInfo().getHostname(), config.getProxyInfo().getPort(), config.getProxyInfo().getScheme());

                        //包含账号密码的代理
//                        CredentialsProvider provider = new BasicCredentialsProvider();
//                        provider.setCredentials(new AuthScope(httpProxy), new UsernamePasswordCredentials("username", "password"));

                        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpProxy);
                        // setProxy  setRoutePlanner最终都是为了生成 DefaultProxyRoutePlanner, 二者使用一个即可
                        // RequestConfig.Builder 的 setProxy() 也一样最后都是用于生成 DefaultProxyRoutePlanner (RequestConfig的方式可以灵活的使 CloseableHttpClient 发送请求的时候使用或者不使用代理)
                        httpClientBuilder.setRoutePlanner(routePlanner).setProxy(httpProxy);
                    }
                    httpClient = httpClientBuilder.build();
                    
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
                    return httpClient;
                }
            }
        }
        return httpClient;
    }

    public static boolean getBytes(String url, Map<String, String> headers, Map<String, String> params, String fileName, String dir, String rootDir)
    {
        try (CloseableHttpClient client = getClient(new Config()))
        {
            url = initUrlParams(url, params);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            /*initHeader(httpGet, headers);*/
            httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            return client.execute(httpGet, new CurResponseHandlerBytes(fileName, dir, rootDir));
        }
        catch (Exception e)
        {
            throw new RuntimeException("getBytes error", e);
        }
    }

    public static <T> T post(String url, Map<String, String> headers, Map<String, String> body, final TypeReference<T> typeReference)
    {
        try (CloseableHttpClient client = getClient(null == CONFIG_THREAD_LOCAL.get() ? new Config() : CONFIG_THREAD_LOCAL.get()))
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(null == REQUEST_CONFIG_THREAD_LOCAL.get() ? requestConfig : REQUEST_CONFIG_THREAD_LOCAL.get());
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(cn.hutool.json.JSONUtil.toJsonStr(body));
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            return client.execute(httpPost, new CurResponseHandler<T>(typeReference));
        }
        catch (Exception e)
        {
            throw new RuntimeException("T post error", e);
        }
    }

    public static <T> T get(String url,
                            Map<String, String> headers,
                            Map<String, String> params,
                            TypeReference<T> tTypeReference,
                            int times)
    {
        long start = System.currentTimeMillis();
        HttpGet httpGet = null;
        try
        {
            CloseableHttpClient client = getClient(null == CONFIG_THREAD_LOCAL.get() ? new Config() : CONFIG_THREAD_LOCAL.get());
            if (client == null)
            {
                throw new RuntimeException("client is null");
            }
            url = initUrlParams(url, params);
            httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            initHeader(httpGet, headers);
            T result = client.execute(httpGet, new CurResponseHandler<>(tTypeReference));
            return result;
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

    public static String getString(String url, Map<String, String> headers, Map<String, String> params)
    {
        try (CloseableHttpClient client = getClient(new Config()))
        {
            url = initUrlParams(url, params);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
//            initHeader(httpGet, headers);
            httpGet.addHeader("Content-Type", "text/html; charset=UTF-8");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
            httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpGet.addHeader("Connection", "close");
            String result = client.execute(httpGet, new CurResponseHandlerString());
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException("getString error", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("getString Exception error", e);
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
            params.entrySet().stream().forEach(entry -> finalBuilder.setParameter(entry.getKey(), entry.getValue()));
            url = builder.build().toString();
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
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
        headers.entrySet().stream().forEach((entry) ->
        {
            httpRequestBase.addHeader(entry.getKey(), entry.getValue());
        });
    }

    static class CurResponseHandler<T> implements ResponseHandler<T>
    {

        private TypeReference<T> typeReference;

        public CurResponseHandler(TypeReference<T> typeReference)
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
            return JSONUtil.fromJson(EntityUtils.toString(httpEntity), typeReference);
        }
    }

    static class CurResponseHandlerString implements ResponseHandler<String>
    {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
        {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200 || status >= 300)
            {
                return null;
            }
            HttpEntity httpEntity = response.getEntity();
            if (null != httpEntity)
            {
                return EntityUtils.toString(httpEntity);
            }
            return null;
        }
    }

    static class CurResponseHandlerBytes implements ResponseHandler<Boolean>
    {
        private String fileName;
        private String dir;

        private String targetDir;

        public CurResponseHandlerBytes(String fileName, String dir, String rootDir)
        {
            this.fileName = fileName;
            this.dir = dir;/*dir.replaceAll(Constants.REGEX_FILE_NAME, "")*/
            targetDir = /*CommonConfig.getInstance().getFileSaveDir()*/rootDir + File.separator + this.dir + File.separator;
            if (!new File(targetDir).exists())
            {
                new File(targetDir).mkdirs();
            }
        }


        @Override
        public Boolean handleResponse(HttpResponse response) throws ClientProtocolException, IOException
        {
            int status = response.getStatusLine().getStatusCode();
            if (status < 200 || status >= 300)
            {
                return false;
            }
            HttpEntity httpEntity = response.getEntity();
            if (null != httpEntity && null != httpEntity.getContent())
            {
                InputStream inputStream = httpEntity.getContent();
                FileOutputStream randomAccessFile = null;
                byte[] buffer = null;
                try
                {
                    randomAccessFile = new FileOutputStream(new File(targetDir + fileName));
                    buffer = new byte[1024 * 100];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1)
                    {
                        randomAccessFile.write(buffer, 0, len);
                    }
                    return true;
                }
                finally
                {
                    try
                    {
                        if (null != randomAccessFile)
                            randomAccessFile.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("ScanTask IOException error", e);
                    }
                    try
                    {
                        if (null != inputStream)
                            inputStream.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("ScanTask IOException error", e);
                    }
                    buffer = null;
                    System.gc();
                }
            }
            return false;
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

//            System.setProperty("http.proxyHost", hostname);
//            System.setProperty("http.proxyPort", port + "");
//            System.setProperty("http.proxyUser", username);
//            System.setProperty("http.proxyPassword", passwd);
//            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
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
            os.writeTo(out);
        }

        @Override
        public boolean isStreaming()
        {
            return false;
        }
    }
}