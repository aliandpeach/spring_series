package com.yk.httprequest;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
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
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    
    private static CloseableHttpClient httpClient; // 发送请求的客户端单例
    
    public static CloseableHttpClient getClient(ProxyInfo proxyInfo) throws GeneralSecurityException, IOException
    {
        if (null == httpClient)
        {
            synchronized (HttpClientUtil.class)
            {
                if (null == httpClient)
                {
                    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                    sslContextBuilder.loadTrustMaterial((chain, authType) -> true);

                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

                    KeyManagerFactory factory = KeyManagerFactory.getInstance("SunX509");
                    KeyStore key = KeyStore.getInstance("JKS");
                    key.load(new FileInputStream("D:\\idea_workspace\\development_tool\\apache-tomcat-9.0.41_https\\conf\\ssl\\broker.ks"), "Spinfo@0123".toCharArray());
                    factory.init(key, "Spinfo@0123".toCharArray());
                    sslContext.init(factory.getKeyManagers(), new TrustManager[]{new NullX509TrustManager()}, new SecureRandom());

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

                    if (null != proxyInfo && proxyInfo.isProxy() && null != proxyInfo.getUsername() && null != proxyInfo.getPasswd())
                    {
                        // 需要用户名密码的代理
                        HttpHost httpHost = new HttpHost(proxyInfo.getHostname(), proxyInfo.getPort(), "http");
                        CredentialsProvider provider = new BasicCredentialsProvider();
                        provider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(proxyInfo.getUsername(), proxyInfo.getPasswd()));
                        httpClientBuilder.setDefaultCredentialsProvider(provider);
                    }
                    else if (null != proxyInfo && proxyInfo.isProxy())
                    {
                        HttpHost httpHost = new HttpHost(proxyInfo.getHostname(), proxyInfo.getPort(), "http");
                        // 不需要用户名密码的代理
                        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpHost);
                        // setProxy  setRoutePlanner最终都是为了生成 DefaultProxyRoutePlanner, 二者使用一个即可
                        httpClientBuilder.setRoutePlanner(routePlanner).setProxy(httpHost);
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
        try (CloseableHttpClient client = getClient(new ProxyInfo(false, null, 0, null, null, null)))
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
        catch (IOException e)
        {
            throw new RuntimeException("getBytes error", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("getBytes error", e);
        }
    }

    public static <T> T post(String url, Map<String, String> headers, Map<String, String> body, final TypeReference<T> typeReference)
    {
        try (CloseableHttpClient client = getClient(new ProxyInfo(false, null, 0, null, null, null)))
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            initHeader(httpPost, headers);

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(cn.hutool.json.JSONUtil.toJsonStr(body));
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());

            T result = client.execute(httpPost, new CurResponseHandler<T>(typeReference));
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException("T post error", e);
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
            CloseableHttpClient client = getClient(new ProxyInfo(false, null, 0, null, null, null));
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
        try (CloseableHttpClient client = getClient(new ProxyInfo(false, null, 0, null, null, null)))
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

        private String username;

        private String passwd;

        private String scheme = "http";

        private Authenticator auth;

        public ProxyInfo(boolean proxy, String hostname, int port, String username, String passwd, String scheme)
        {
            this.proxy = proxy;
            this.hostname = hostname;
            this.port = port;
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
//            System.setProperty("http.proxyHost", hostname);
//            System.setProperty("http.proxyPort", port + "");
//            System.setProperty("http.proxyUser", username);
//            System.setProperty("http.proxyPassword", passwd);
//            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
//            Authenticator.setDefault(auth);
        }
    }
}
