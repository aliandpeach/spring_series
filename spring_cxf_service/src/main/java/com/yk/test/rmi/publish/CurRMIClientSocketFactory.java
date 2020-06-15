package com.yk.test.rmi.publish;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.security.*;
import java.security.cert.CertificateException;

public class CurRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
{
    private static final long serialVersionUID = -604190765637337735L;

    @Override
    public Socket createSocket(String host, int port) throws IOException
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
            SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory()
                    .createSocket(InetAddress.getByName(host), port);
            return sslSocket;
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
