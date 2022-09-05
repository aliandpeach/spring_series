package com.yk;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.Collection;

@SpringBootApplication
//@EnableAsync
//@EnableScheduling
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@ServletComponentScan
public class JspApplication extends SpringBootServletInitializer
{
    private static final Logger logger = LoggerFactory.getLogger(JspApplication.class);

    public static void main(String[] args)
    {
        System.setProperty("log.home", System.getProperty("user.dir"));
        logger.info("user.dir = {}", System.getProperty("user.dir"));
        SpringApplication application = new SpringApplication();
        application.run(JspApplication.class);
    }
    
    
    /**
     * springboot-war 步骤(1)
     * 增加@Override configure方法
     */
    /**
     * 打包为war包后的启动配置
     *
     * @param builder
     * @return
     */
    /*@Override
    public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }*/
    
    /**
     * springboot-war 步骤(2)
     * 注释掉这些SSL配置
     */
    @Bean
    public ServletWebServerFactory servletContainerInitializer()
    {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
        {
            @Override
            public void postProcessContext(Context context)
            {
                // 配置静态资源访问
//                SecurityConstraint constraint1 = new SecurityConstraint();
//                constraint1.setUserConstraint("NONE");
//                SecurityCollection collection1 = new SecurityCollection();
//                collection1.addPattern("/static/");
//                constraint1.addCollection(collection1);
//                context.addConstraint(constraint1);
                
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer()
        {
            public void customize(Connector connector)
            {
                connector.setPort(9024);
                connector.setSecure(true);
                connector.setScheme("https");
                
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setKeystoreFile("key/website.ks");
                protocol.setKeyPass("Admin@1234");
                protocol.setKeystorePass("Admin@123");
                protocol.setKeystoreType("JKS");
                protocol.setSSLEnabled(true);
                protocol.setClientAuth("false");
            }
        });
        
        /**
         * 额外增加的Connector
         */
        Connector connectorAdditional = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connectorAdditional.setPort(9025);
        connectorAdditional.setSecure(true);
        connectorAdditional.setScheme("https");
        
        Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connectorAdditional.getProtocolHandler();
        http11NioProtocol.setKeystoreFile("key/website.ks");
        http11NioProtocol.setKeyPass("Admin@1234");
        http11NioProtocol.setKeystorePass("Admin@123");
        http11NioProtocol.setKeystoreType("JKS");
        http11NioProtocol.setSSLEnabled(true);
        http11NioProtocol.setClientAuth("false");
        
        tomcat.addAdditionalTomcatConnectors(connectorAdditional);
        return tomcat;
    }
    
    /**
     * customize方法中的Connector与 servletContainerInitializer-customize的Connector是同一个，
     * 所以该Bean的customize会覆盖上面的customize
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer()
    {
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>()
        {
            public void customize(TomcatServletWebServerFactory factory)
            {
                Collection<TomcatConnectorCustomizer> list = factory.getTomcatConnectorCustomizers();
                for (TomcatConnectorCustomizer tomcatConnectorCustomizer : list)
                {
                    Class<?> clazz = tomcatConnectorCustomizer.getClass();
                    logger.info("class name {}", clazz.getName());
                }
                factory.addConnectorCustomizers(new TomcatConnectorCustomizer()
                {
                    public void customize(Connector connector)
                    {
                        connector.setPort(9026);
                        connector.setSecure(true);
                        connector.setScheme("https");

                        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                        protocol.setKeystoreFile("key/website.ks");
                        protocol.setKeyPass("Admin@1234");
                        protocol.setKeystorePass("Admin@123");
                        protocol.setKeystoreType("JKS");
                        protocol.setSSLEnabled(true);
                        protocol.setClientAuth("false");
                    }
                });
                // SpringBoot 给内置tomcat的docBase目录默认是 src/main/webapp public static
                // factory.setDocumentRoot(new File(""));
                logger.info("==================JspApplication=================");
                logger.info(JspApplication.class.getClassLoader().getResource("").getPath());
                logger.info("==================JspApplication=================");
            }
        };
    }
}
