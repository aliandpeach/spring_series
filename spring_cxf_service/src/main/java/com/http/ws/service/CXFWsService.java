package com.http.ws.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.yk.test.restful.RestfulPublish;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.configuration.security.ClientAuthentication;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
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

    public void publish()
    {
        try
        {
            JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean();
            //代码实现SSL
            configSSL();
            serverFactoryBean.setServiceClass(HelloService.class);
            serverFactoryBean.setAddress("https://localhost:9096/ws");
            Server server = serverFactoryBean.create();
            String endpoint = server.getEndpoint().getEndpointInfo().getAddress();
            System.out.println("Server started at " + endpoint);
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

    public void configSSL() throws GeneralSecurityException, IOException
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

        ClientAuthentication clientAuthentication = new ClientAuthentication();
        clientAuthentication.setRequired(false);
        clientAuthentication.setWant(true);

//        FiltersType filtersTypes = new FiltersType();
//        filtersTypes.getInclude().add(".*_EXPORT_.*");
//        filtersTypes.getInclude().add(".*_EXPORT1024_.*");
//        filtersTypes.getInclude().add(".*_WITH_DES_.*");
//        filtersTypes.getInclude().add(".*_WITH_NULL_.*");
//        filtersTypes.getExclude().add(".*_DH_anon_.*");

        TLSServerParameters tlsServerParameters = new TLSServerParameters();
        tlsServerParameters.setKeyManagers(keyManagerFactory.getKeyManagers());
        tlsServerParameters.setTrustManagers(trustManagerFactory.getTrustManagers());
        tlsServerParameters.setClientAuthentication(clientAuthentication);
//        tlsServerParameters.setCipherSuitesFilter(filtersTypes);
        tlsServerParameters.setSecureSocketProtocol("TLSv1.2");

        JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
        factory.setTLSServerParametersForPort(9096, tlsServerParameters);
        JettyHTTPServerEngine engine = factory.createJettyHTTPServerEngine(null, 9096, "https");
        // CXF设置 JettyHTTPServerEngine缓存起来, 根据端口将来选择使用哪个Jetty服务器实例
        factory.initComplete();
    }

    public static void main(String[] args)
    {
        new CXFWsService().publish();
    }
}
