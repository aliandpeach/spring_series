package com.yk.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

public class HttpServerStarter
{
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException
    {

//        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(443), 0);
//        SSLContext context = SSLContext.getInstance("TLSv1.2");
//        context.init(null, new TrustManager[]{new X509TrustManager()
//        {
//            @Override
//            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
//            {
//
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
//            {
//
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers()
//            {
//                return new X509Certificate[0];
//            }
//        }}, null);
//        httpsServer.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getInstance("TLSv1.2")));

        //创建一个HttpServer实例，并绑定到指定的IP地址和端口号
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        //创建一个HttpContext，将路径为/server 请求映射到MyHttpHandler处理器
        httpServer.createContext("/server", new MyHttpHandler());
        //设置服务器的线程池对象
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        //启动服务器
        httpServer.start();
    }
}
