package com.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/10/08 10:38:08
 */
public class ProxySystemTest
{
    public static void main(String[] args)
    {
        Properties prop = System.getProperties();
        // 设置http访问要使用的代理服务器的地址
        prop.setProperty("http.proxyHost", "183.45.78.31");
        // 设置http访问要使用的代理服务器的端口
        prop.setProperty("http.proxyPort", "8080");
        // 设置不需要通过代理服务器访问的主机，可以使用*通配符，多个地址用|分隔
        prop.setProperty("http.nonProxyHosts", "localhost|192.168.0.*");
        // 设置安全访问使用的代理服务器地址与端口
        // 它没有https.nonProxyHosts属性，它按照http.nonProxyHosts 中设置的规则访问
        prop.setProperty("https.proxyHost", "183.45.78.31");
        prop.setProperty("https.proxyPort", "443");
        // 使用ftp代理服务器的主机、端口以及不需要使用ftp代理服务器的主机
        prop.setProperty("ftp.proxyHost", "183.45.78.31");
        prop.setProperty("ftp.proxyPort", "21");
        prop.setProperty("ftp.nonProxyHosts", "localhost|192.168.0.*");
        // socks代理服务器的地址与端口
        prop.setProperty("socksProxyHost", "183.45.78.31");
        prop.setProperty("socksProxyPort", "1080");
        // 设置登陆到代理服务器的用户名和密码
        Authenticator.setDefault(new MyAuthenticator("userName", "Password"));
    }

    static class MyAuthenticator extends Authenticator
    {
        private String user = "";
        private String password = "";

        public MyAuthenticator(String user, String password)
        {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }
}
