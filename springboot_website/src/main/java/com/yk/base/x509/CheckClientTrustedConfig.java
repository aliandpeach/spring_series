package com.yk.base.x509;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.EnumSet;

@Configuration
public class CheckClientTrustedConfig implements ServletContextInitializer
{
    private static final Logger logger = LoggerFactory.getLogger(CheckClientTrustedConfig.class);

    @Value("${trust.trust-store}")
    private String trustStore;

    @Value("${trust.trust-store-passwd}")
    private String trustStorePasswd;

    @Value("${trust.verify-uri}")
    private String verifyUri;

    @Value("${trust.root-ca-alias}")
    private String rootCAAlias;

    @Getter
    private TrustManager[] trustManagers;

    @Getter
    private Certificate root;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        logger.debug("check client trusted config on startup...");
        if (null == trustStore || null == trustStorePasswd)
        {
            logger.error("trust store path or passwd is null");
            FilterRegistration.Dynamic filter = servletContext.addFilter("checkClientTrustedFilter", new CheckClientTrustedFilter(trustManagers, root, verifyUri));
            filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
            return;
        }
        try(InputStream input = getInputStream(trustStore))
        {
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
            KeyStore trust = KeyStore.getInstance("JKS");
            trust.load(input, trustStorePasswd.toCharArray());
            root = trust.getCertificate(rootCAAlias);
            trustFactory.init(trust);
            trustManagers = trustFactory.getTrustManagers();
        }
        catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e)
        {
            trustManagers = null;
            logger.error("load client trust store error", e);
        }
        FilterRegistration.Dynamic filter = servletContext.addFilter("checkClientTrustedFilter", new CheckClientTrustedFilter(trustManagers, root, verifyUri));
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
    }

    private static InputStream getInputStream(String location) throws IOException
    {
        InputStream input = null;
        try
        {
            File sdk = new File(location);
            if (sdk.exists() && sdk.isFile())
            {
                input = new FileInputStream(sdk);
                return input;
            }
        }
        catch (Exception e)
        {
            logger.error("load truststore " + location + " by new File() error {}", e.getMessage());
        }

        try
        {
            URL url = new URL(location);
            input = url.openStream();
            if (input != null)
            {
                return input;
            }
        }
        catch (IOException e)
        {
            logger.error("load truststore " + location + " by new URL {}", e.getMessage());
        }

        input = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

        if (input == null)
        {
            throw new IOException("无法加载证书文件: " + location);
        }
        return input;
    }
}
