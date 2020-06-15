package com.yk.test.rmi.publish;

import javax.net.ssl.*;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.*;
import java.security.cert.CertificateException;

public class CurRMIServerSocketFactory implements RMIServerSocketFactory, Serializable
{
    private static final long serialVersionUID = 7743751071686902263L;

    private String host;

    public CurRMIServerSocketFactory(String host)
    {
        this.host = host;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException
    {
        try
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream inputStream = CurRMIClientSocketFactory.class.getResourceAsStream("/mytestkeystore");
            keyStore.load(inputStream, "Admin@123".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "Admin@123".toCharArray());
            KeyStore trustStore = KeyStore.getInstance("JKS");
            InputStream inputStream1 = CurRMIClientSocketFactory.class.getResourceAsStream("/mytesttruststore");
            trustStore.load(inputStream1, "Admin@123".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
                    .createServerSocket(port);
            return sslServerSocket;
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (CertificateException e)
        {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
        } catch (KeyStoreException e)
        {
            e.printStackTrace();
        } catch (KeyManagementException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
