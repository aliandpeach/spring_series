package com.yk.connector.http;

import com.yk.core.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * SSL
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/24 11:25
 */
public class UntrustedSSL
{

    private static final UntrustedSSL INSTANCE = new UntrustedSSL();
    private static final TrustedSSL INSTANCE_TRUSTED = new TrustedSSL();
    private static final Logger LOG = LoggerFactory.getLogger(UntrustedSSL.class);

    private SSLContext context;
    private HostnameVerifier verifier;

    private UntrustedSSL()
    {
        try
        {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
            {
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType)
                {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType)
                {
                }
            }};
            SSLContext ssc = SSLContext.getInstance("TLSv1.2");
            ssc.init(null, trustAllCerts, new SecureRandom());

            this.context = ssc;
            this.verifier = (s, session) -> true;
        }
        catch (Throwable t)
        {
            LOG.error(t.getMessage(), t);
        }
    }

    public static SSLContext getSSLContext()
    {
        return INSTANCE.context;
    }

    public static HostnameVerifier getHostnameVerifier()
    {
        return INSTANCE.verifier;
    }

    public static SSLContext getTrustedSSLContext()
    {
        return INSTANCE_TRUSTED.context;
    }

    public static class TrustedSSL
    {
        private SSLContext context;

        private TrustedSSL()
        {
            try(InputStream input = PropertyLoader.getInputStream("cert/sdk.ks"))
            {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
                {
                    public X509Certificate[] getAcceptedIssuers()
                    {
                        return new X509Certificate[]{};
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType)
                    {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType)
                    {
                    }
                }};
                SSLContext ssc = SSLContext.getInstance("TLSv1.2");

                KeyStore key = KeyStore.getInstance("JKS");
                key.load(input, new char[]{'E', '4', '7', 'C', 'F', '2', '1', 'F', '7', '2', '3', '7', '0', '5', 'F', '5', '1', '6', 'F', '5', 'F', '2', 'F', '0', '0', '6', '8', '0', '0', '1', 'F', 'E', '@', 'm', '$', '5', 'S', 'q', '4', 'Q'});
                KeyManagerFactory keyFactory = KeyManagerFactory.getInstance("SunX509");
                keyFactory.init(key, new char[]{'C', '4', '7', '1', '8', '2', 'A', '1', '9', 'F', '4', '0', 'F', '6', '9', 'B', '5', 'C', '0', '2', '2', '6', '6', '6', 'B', '7', '2', 'A', '7', '5', '1', 'C', 'n', 'H', 'X', 'B', '$', 'f', '#', 'T'});

                ssc.init(keyFactory.getKeyManagers(), trustAllCerts, new SecureRandom());

                this.context = ssc;
            }
            catch (Throwable t)
            {
                LOG.error(t.getMessage(), t);
            }
        }
    }
}
