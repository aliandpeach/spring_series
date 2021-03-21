package com.yk;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collection;

@SpringBootApplication
@EnableAsync
@EnableScheduling
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class BlockchainServiceApplication extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        System.setProperty("log.home", System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.dir"));
        SpringApplication application = new SpringApplication();
        application.run(BlockchainServiceApplication.class);
    }

    @Bean
    public ServletWebServerFactory thymeleafContainerInitializer()
    {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
        {
            @Override
            public void postProcessContext(Context context)
            {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector ->
        {
            connector.setPort(21111);
            connector.setSecure(true);
            connector.setScheme("https");

            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            protocol.setKeystoreFile("key/website_a.jks");
            protocol.setKeyPass("Admin@1234");
            protocol.setKeystorePass("Admin@123");
            protocol.setKeystoreType("JKS");
            protocol.setSSLEnabled(true);
            protocol.setClientAuth("false");
        });
        return tomcat;
    }
}
