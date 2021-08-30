package com.http.ws.service;

import com.yk.test.restful.RestfulPublish;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.configuration.security.ClientAuthentication;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/06 10:22:34
 */
@WebService(serviceName = "HelloService")
public class HelloService
{
    @WebMethod(operationName = "sayHello")
    @WebResult(name = "myReturn")
    public String sayHello(@WebParam(name = "name") String name)
    {
        return "hello: " + name;
    }

    public String sayGoodbye(String name)
    {
        return "goodbye: " + name;
    }

    @WebMethod(exclude = true)
    public String sayHello2(String name)
    {
        return "hello " + name;
    }

    /**
     * 参数1：服务的发布地址
     * 参数2：服务的实现者
     * Endpoint  会重新启动一个线程
     */
    public static void main(String[] args) throws Exception
    {
        // 根据ServiceLoader选择使用SUN的服务器或者第三方的服务器（这里默认就使用了CXF的Jetty）
        Endpoint.publish("http://localhost:8086/ws", new HelloService());


        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = RestfulPublish.class.getResourceAsStream("/mytestkeystore");
        keyStore.load(inputStream, "Admin@123".toCharArray());
        keyFactory.init(keyStore, "Admin@123".toCharArray());

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream inputStream1 = RestfulPublish.class.getResourceAsStream("/mytesttruststore");
        trustStore.load(inputStream1, "Admin@123".toCharArray());
        trustFactory.init(trustStore);

        JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
        TLSServerParameters tlsServerParameters = new TLSServerParameters();
        tlsServerParameters.setKeyManagers(keyFactory.getKeyManagers());
        tlsServerParameters.setTrustManagers(trustFactory.getTrustManagers());
        tlsServerParameters.setSecureSocketProtocol("TLSv1.2");

        ClientAuthentication clientAuthentication = new ClientAuthentication();
        clientAuthentication.setRequired(false);
        clientAuthentication.setWant(true);
        tlsServerParameters.setClientAuthentication(clientAuthentication);

        // CXF设置 JettyHTTPServerEngine缓存起来, 根据端口将来选择使用哪个Jetty服务器实例
        factory.setTLSServerParametersForPort(9096, tlsServerParameters);

        HelloService helloService = new HelloService();
        Endpoint endpoint = Endpoint.create(helloService);
        endpoint.publish("https://localhost:9096/ws");
    }
}
