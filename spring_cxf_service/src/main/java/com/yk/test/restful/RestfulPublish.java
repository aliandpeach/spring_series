package com.yk.test.restful;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.configuration.security.ClientAuthentication;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestfulPublish
{
    private List<Class<?>> clazzs = new ArrayList<>();

    private List<Object> objects = new ArrayList<>();

    private RestfulPublish()
    {
    }

    public void publish()
    {
        try
        {
            JAXRSServerFactoryBean restServer = new JAXRSServerFactoryBean();
            //代码实现SSL
            configSSL();
            //配置实现SSL
            //官方参考网址http://cxf.apache.org/docs/secure-jax-rs-services.html
//            SpringBusFactory springBusFactory = new SpringBusFactory();
//            Bus bus = springBusFactory.createBus("bean.xml");
//            restServer.setBus(bus);
            restServer.setResourceClasses(clazzs);
            restServer.setServiceBeanObjects(objects);
            restServer.setProvider(new JacksonJsonProvider());
            restServer.setAddress("https://127.0.0.1:9095/");
            restServer.create();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static RestfulPublish getInstance()
    {
        return RestfulPublishHolder.instance;
    }

    private static class RestfulPublishHolder
    {
        public static RestfulPublish instance = new RestfulPublish();
    }

    public synchronized void addClazzs(Class<?> clazz)
    {
        this.clazzs.add(clazz);
    }

    public synchronized void addObjects(Object object)
    {
        this.objects.add(object);
    }

    public void configSSL() throws GeneralSecurityException, IOException
    {
        //使用keytool生成私钥和信任库
        //keytool -alias mytestkeystore -genkeypair -keyalg RSA  -keysize 2048 -validity 365 -keystore D:\cert\mytestkeystore
        //keytool -exportcert -alias mytestkeystore -keystore D:\cert\mytestkeystore -file D:\cert\mytestcert.crt
        //keytool -importcert -trustcacerts -alias mytestkeystore -file D:\cert\mytestcert.crt  -keystore D:\cert\mytesttruststore
        //配置ssl
        TLSServerParameters tlsServerParameters = new TLSServerParameters();

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = RestfulPublish.class.getResourceAsStream("/mytestkeystore");
        keyStore.load(inputStream, "Admin@123".toCharArray());
        keyManagerFactory.init(keyStore, "Admin@123".toCharArray());
        tlsServerParameters.setKeyManagers(keyManagerFactory.getKeyManagers());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream inputStream1 = RestfulPublish.class.getResourceAsStream("/mytesttruststore");
        trustStore.load(inputStream1, "Admin@123".toCharArray());
        trustManagerFactory.init(trustStore);
        tlsServerParameters.setTrustManagers(trustManagerFactory.getTrustManagers());

//        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
//        SSLContext.setDefault(sslContext);

        ClientAuthentication clientAuthentication = new ClientAuthentication();
        clientAuthentication.setRequired(false);
        clientAuthentication.setWant(true);
        tlsServerParameters.setClientAuthentication(clientAuthentication);

        tlsServerParameters.setSecureSocketProtocol("TLSv1.2");

        JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
        // CXF设置 JettyHTTPServerEngine缓存起来, 根据端口将来选择使用哪个Jetty服务器实例
        factory.setTLSServerParametersForPort(9095, tlsServerParameters);
        factory.initComplete();
    }
}
