package com.yk.base.x509;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/17 17:11:12
 *
 * clientAuth=want 只有客户端发来证书才会进行认证
 */
public class ServerX509TrustManager implements X509TrustManager
{
    private Logger logger = LoggerFactory.getLogger("base");

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        logger.info("checkClientTrusted...");
        logger.info(s);
        logger.info(Arrays.toString(x509Certificates));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        logger.info("checkServerTrusted...");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }
}
