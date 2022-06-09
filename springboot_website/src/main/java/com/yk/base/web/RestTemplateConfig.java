package com.yk.base.web;

import com.yk.httprequest.HttpClientUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig
{
    @Bean
    public ClientHttpRequestFactory factory()
    {
        HttpComponentsClientHttpRequestFactory httpRequestFactory;
        try
        {
            HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 60010, "http");
            HttpClientUtil.Config config = new HttpClientUtil.Config();
            // 使用 http-client组件
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory(new HttpClientUtil(config.ofProxy(proxyInfo)).httpClient);
            httpRequestFactory.setConnectTimeout(240000);
            httpRequestFactory.setReadTimeout(240000);
            return httpRequestFactory;
        }
        catch (Exception e)
        {
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//            httpRequestFactory.setConnectTimeout(25000);
//            httpRequestFactory.setReadTimeout(25000);
            return httpRequestFactory;
        }
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory)
    {
        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();
        converterList.add(new MappingJackson2HttpMessageConverter());

        // FormHttpMessageConverter可读取HttpEntity<MultiValueMap<String, Object>>的内容参数为form-data报文格式
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        // 下面一行配置, FormHttpMessageConverter就可解析MultiValueMap, 使FormHttpMessageConverter支持application/octet-stream格式;
        // 在 RestTemplate$HttpEntityRequestCallback.doWithRequest, 就可以正常读取文件流类型
        formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);

        // 下面两行的配置, FormHttpMessageConverter就可以解析对象参数, 使其转为json格式
        formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
        formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
        converterList.add(formHttpMessageConverter);

//        converterList.add(new MappingJackson2XmlHttpMessageConverter());
        converterList.add(new StringHttpMessageConverter());
        converterList.add(new ByteArrayHttpMessageConverter());
        restTemplate.setMessageConverters(converterList);
        return restTemplate;
    }

    @Bean
    public RestTemplate httpURLConnectionRestTemplate()
    {
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory()
        {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException
            {
                if (!(connection instanceof HttpsURLConnection))
                {
                    super.prepareConnection(connection, httpMethod);
                    return;
                }
                try
                {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
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
                    httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                    httpsURLConnection.setHostnameVerifier((s, sslSession) -> true);
                    super.prepareConnection(httpsURLConnection, httpMethod);
                    return;
                }
                catch (Exception e)
                {
                    super.prepareConnection(connection, httpMethod);
                }
            }
        });
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();
        converterList.add(new MappingJackson2HttpMessageConverter());
        converterList.add(new FormHttpMessageConverter());
//        converterList.add(new MappingJackson2XmlHttpMessageConverter());
        converterList.add(new StringHttpMessageConverter());
        converterList.add(new ByteArrayHttpMessageConverter());
        restTemplate.setMessageConverters(converterList);
        return restTemplate;
    }
}
