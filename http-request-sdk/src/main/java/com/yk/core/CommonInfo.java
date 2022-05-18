package com.yk.core;

import com.yk.others.NotEmpty;
import com.yk.others.Value;
import lombok.Data;

/**
 * 公共参数
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/25 10:13
 */
@Data
public class CommonInfo
{
    @NotEmpty
    @Value("activemq.broker.url")
    private String brokerUrl;

    @NotEmpty
    @Value("activemq.username")
    private String username;

    @Value("activemq.password")
    private String password;

    @NotEmpty
    @Value("activemq.keystore")
    private String keystore;

    @NotEmpty
    @Value("activemq.keystore.password")
    private String keystorePasswd;

    @NotEmpty
    @Value("activemq.truststore")
    private String truststore;

    @NotEmpty
    @Value("activemq.truststore.password")
    private String truststorePasswd;

    @NotEmpty
    @Value(value = "activemq.send.timeout", type = Integer.class)
    private int sendTimeout = 120000;

    @NotEmpty
    @Value(value = "activemq.close.timeout", type = Integer.class)
    private int closeTimeout = 120000;

    @NotEmpty
    @Value("ftp.username")
    private String ftpUsername;

    @Value("ftp.password")
    private String ftpPassword;

    @Value("ftp.key")
    private String ftpKey;

    @NotEmpty
    @Value("ftp.ip")
    private String ip;

    @NotEmpty
    @Value(value = "ftp.port", type = Integer.class)
    private int port;

    @Value("http.proxy.host")
    private String proxyHost;

    @Value(value = "http.proxy.port", type = Integer.class)
    private int proxyPort;

    @Value("http.proxy.username")
    private String proxyUser;

    @Value("http.proxy.passwd")
    private String proxyPasswd;

    @Value(value = "force.reconnect.activemq", type = Boolean.class)
    private boolean forceReConnectActiveMQ;

    private boolean initActivemq = true;
}
