package com.http.ws.service;

import com.yk.test.restful.RestfulPublish;
import com.yk.test.ws.sm.impl.WSSmCommUpperImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.configuration.security.ClientAuthentication;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/06 11:43:47
 */
public class CXFWsService
{
    private List<Class<?>> clazzs = new ArrayList<>();

    private List<Object> objects = new ArrayList<>();

    private CXFWsService()
    {
    }

    /**
     * 方法 2：手动发布（使用 JaxWsServerFactoryBean + 自定义配置的Jetty） + Jetty服务器
     *
     * http://localhost:10081/ws?wsdl
     */
    public void publish()
    {
        try
        {
            //代码实现SSL
            Bus bus = configSSL();
            Endpoint endpoint = new EndpointImpl(bus, new WriteService());
            endpoint.publish("http://localhost:10081/ws/write");
            // Endpoint.publish("http://localhost:10081/ws/write", new WriteService());

            JaxWsServerFactoryBean serverFactoryBean0 = new JaxWsServerFactoryBean();
            serverFactoryBean0.setBus(bus);
            serverFactoryBean0.setServiceClass(HelloService.class);
            // setAddress在非web使用jetty场景中, 必须指定http://ip:port
            serverFactoryBean0.setAddress("http://localhost:10081/ws/hello");
            Server server0 = serverFactoryBean0.create();
            String endpoint0 = server0.getEndpoint().getEndpointInfo().getAddress();
            // http://localhost:10081/ws/hello?wsdl
            System.out.println("Server0 started at " + endpoint0);

            JaxWsServerFactoryBean serverFactoryBean1 = new JaxWsServerFactoryBean();
            serverFactoryBean1.setBus(bus);
            serverFactoryBean1.setServiceClass(SayService.class);
            serverFactoryBean1.setAddress("http://localhost:10081/ws/say");
            Server server1 = serverFactoryBean1.create();
            String endpoint1 = server1.getEndpoint().getEndpointInfo().getAddress();
            // http://localhost:10081/ws/say?wsdl
            System.out.println("Server1 started at " + endpoint1);

            JaxWsServerFactoryBean serverFactoryBean2 = new JaxWsServerFactoryBean();
            serverFactoryBean2.setBus(bus);
            serverFactoryBean2.setServiceClass(WSSmCommUpperImpl.class);
            serverFactoryBean2.setAddress("http://localhost:10081/WSSmCommUpper/WSSmCommUpper");
            Server server2 = serverFactoryBean2.create();
            String endpoint2 = server2.getEndpoint().getEndpointInfo().getAddress();
            System.out.println("Server1 started at " + endpoint1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static CXFWsService getInstance()
    {
        return CXFWsService.CXFWsServiceHolder.instance;
    }

    private static class CXFWsServiceHolder
    {
        public static CXFWsService instance = new CXFWsService();
    }

    public synchronized void addClazz(Class<?> clazz)
    {
        this.clazzs.add(clazz);
    }

    public synchronized void addObjects(Object object)
    {
        this.objects.add(object);
    }

    public Bus configSSL() throws GeneralSecurityException, IOException
    {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = RestfulPublish.class.getResourceAsStream("/mytestkeystore");
        keyStore.load(inputStream, "Admin@123".toCharArray());
        keyManagerFactory.init(keyStore, "Admin@123".toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream inputStream1 = RestfulPublish.class.getResourceAsStream("/mytesttruststore");
        trustStore.load(inputStream1, "Admin@123".toCharArray());
        trustManagerFactory.init(trustStore);

//        ClientAuthentication clientAuthentication = new ClientAuthentication();
//        clientAuthentication.setRequired(false);
//        clientAuthentication.setWant(true);

//        FiltersType filtersTypes = new FiltersType();
//        filtersTypes.getInclude().add(".*_EXPORT_.*");
//        filtersTypes.getInclude().add(".*_EXPORT1024_.*");
//        filtersTypes.getInclude().add(".*_WITH_DES_.*");
//        filtersTypes.getInclude().add(".*_WITH_NULL_.*");
//        filtersTypes.getExclude().add(".*_DH_anon_.*");

//        TLSServerParameters tlsServerParameters = new TLSServerParameters();
//        tlsServerParameters.setKeyManagers(keyManagerFactory.getKeyManagers());
//        tlsServerParameters.setTrustManagers(trustManagerFactory.getTrustManagers());
//        tlsServerParameters.setClientAuthentication(clientAuthentication);
////        tlsServerParameters.setCipherSuitesFilter(filtersTypes);
//        tlsServerParameters.setSecureSocketProtocol("TLSv1.2");

        Bus bus = new SpringBus();
        JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
        factory.setBus(bus);
//        factory.setTLSServerParametersForPort("localhost", 10081, tlsServerParameters);
        JettyHTTPServerEngine engine = factory.createJettyHTTPServerEngine("localhost", 10081, "http");
        engine.setMaxIdleTime(30000); // 设置最大空闲时间（毫秒）
        // CXF设置 JettyHTTPServerEngine缓存起来, 根据端口将来选择使用哪个Jetty服务器实例
        return bus;
    }

    public static void main(String[] args)
    {
        new CXFWsService().publish();
    }
}
