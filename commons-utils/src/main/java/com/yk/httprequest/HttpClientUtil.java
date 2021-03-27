package com.yk.httprequest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClientUtil
{
    private static Logger logger = LoggerFactory.getLogger("request");
    
    //setConnectTimeout：设置连接超时时间，单位毫秒。
    //setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
    //setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(22000).setConnectionRequestTimeout(12000)
            .setSocketTimeout(22000).build();
    
    private static CloseableHttpClient getClient()
    {
        
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;//连接池管理类
        SSLContextBuilder sslContextBuilder = null;//管理Https连接的上下文类
        try
        {
            sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(new TrustStrategy()
            {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                    return true;
                }
            });
            
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, new SecureRandom());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier()
            {
                public boolean verify(String s, SSLSession sslSession)
                {
                    return true;
                }
            });
            Registry<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();
            poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder);
            poolingHttpClientConnectionManager.setMaxTotal(200);
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);
            
            
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory)
                    .setConnectionManager(poolingHttpClientConnectionManager)
                    .setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig)
                    .build();
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
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
                }
            });
            return httpClient;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean getBytes(String url, Map<String, String> headers, Map<String, String> params, String fileName, String dir, String rootDir)
    {
        CloseableHttpClient client = null;
        try
        {
            client = getClient();
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
        finally
        {
            
            try
            {
                if (null != client)
                {
                    client.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    public static <T> T post(String url, Map<String, String> headers, Map<String, String> body, final TypeReference<T> typeReference)
    {
        CloseableHttpClient client = null;
        try
        {
            client = getClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            initHeader(httpPost, headers);
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
        finally
        {
            
            try
            {
                if (null != client)
                {
                    client.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    public static <T> T get(String url,
                            Map<String, String> headers,
                            Map<String, String> params,
                            TypeReference<T> tTypeReference,
                            int times)
    {
        long start = System.currentTimeMillis();
        CloseableHttpClient client = null;
        try
        {
            client = getClient();
            url = initUrlParams(url, params);
            HttpGet httpGet = new HttpGet(url);
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
            
            try
            {
                if (null != client)
                {
                    client.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    public static String getString(String url, Map<String, String> headers, Map<String, String> params)
    {
        CloseableHttpClient client = null;
        try
        {
            client = getClient();
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
        finally
        {
            
            try
            {
                if (null != client)
                {
                    client.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
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
}