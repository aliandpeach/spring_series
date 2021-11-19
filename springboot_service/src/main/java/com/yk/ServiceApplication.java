package com.yk;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.Banner;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collection;

@SpringBootApplication
@EnableAsync
@EnableScheduling
// 注解类的说明是为了注册@WebListener@WebFilter@WebServlet 但同时也说明了内置tomcat才有用 本工程采用外置tomcat不需要该注解,一样可以生效
@ServletComponentScan
@PropertySources({@PropertySource("classpath:Hikari.properties")})
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceApplication {
    private static final String KEYPASS = "Admin@1234";
    private static final String KEYSTOREPASS = "Admin@123";
    
    public static void main(String[] args) {
        System.setProperty("log.home", System.getProperty("user.dir"));
        Banner banner = new ResourceBanner(new ClassPathResource("banner.txt"));
        /*SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder.main(ServiceApplication.class);
        builder.bannerMode(Banner.Mode.CONSOLE);
        builder.banner(banner);
        builder.run(args);*/

        SpringApplication application = new SpringApplication();
        application.setMainApplicationClass(ServiceApplication.class);
        application.setBanner(banner);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(ServiceApplication.class);
    }

    @Bean
    public ServletWebServerFactory servletContainerInitializer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            public void postProcessContext(Context context) {
                // 配置静态资源访问
                /*SecurityConstraint constraint1 = new SecurityConstraint();
                constraint1.setUserConstraint("NONE");
                SecurityCollection collection1 = new SecurityCollection();
                collection1.addPattern("/static/");
                constraint1.addCollection(collection1);
                context.addConstraint(constraint1);*/

                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);

                LoginConfig loginConfig = new LoginConfig();
                loginConfig.setAuthMethod("CLIENT-CERT");
                loginConfig.setRealmName("Client Cert Users-only Area");
                context.setLoginConfig(loginConfig);
            }
        };
        tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            public void customize(Connector connector) {
                connector.setPort(9097);
                connector.setSecure(true);
                connector.setScheme("https");

                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setKeystoreFile("key/website.ks");
                protocol.setKeyPass(KEYPASS);
                protocol.setKeystorePass(KEYSTOREPASS);
                protocol.setKeystoreType("JKS");
                protocol.setSSLEnabled(true);
                protocol.setClientAuth("false");
            }
        });

        /**
         * 额外增加的Connector
         */
        Connector connectorAdditional = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connectorAdditional.setPort(9099);
        connectorAdditional.setSecure(true);
        connectorAdditional.setScheme("https");

        Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connectorAdditional.getProtocolHandler();
        http11NioProtocol.setKeystoreFile("key/website.ks");
        http11NioProtocol.setKeyPass(KEYPASS);
        http11NioProtocol.setKeystorePass(KEYSTOREPASS);
        http11NioProtocol.setKeystoreType("JKS");
        http11NioProtocol.setSSLEnabled(true);
        http11NioProtocol.setClientAuth("false");

        tomcat.addAdditionalTomcatConnectors(connectorAdditional);
        return tomcat;
    }

    /**
     * customize方法中的Connector与 servletContainerInitializer-customize的Connector是同一个，
     * 所以该Bean的customize会覆盖上面的customize
     *
     *
     * TomcatServletWebServerFactory - getWebServer
     *     一个 TomcatServletWebServerFactory 对象中只有一个Connector 对象,
     *     自定义的 WebServerFactoryCustomizer bean会在启动过程中在 WebServerFactoryCustomizerBeanPostProcessor 注入 TomcatServletWebServerFactory bean
     *     而 TomcatServletWebServerFactory bean 是在 ServletWebServerFactoryConfiguration 自动装配的 ( 在缺失 TomcatServletWebServerFactory bean的时候才装配)
     *     ( 但是我们自定义了 servletContainerInitializer方法也就自定义了 TomcatServletWebServerFactory bean )
     *   因此, 这里的 Connector 对象, 和 servletContainerInitializer 的 Connector是同一个 ( TomcatServletWebServerFactory bean 全局就一个, 就是我们自定义的servletContainerInitializer方法生成的)
     *
     *
     * 所以一般来说, 构造一个 WebServerFactoryCustomizer 来对 SpringBoot自动装配的 TomcatServletWebServerFactory 进行修改就够了
     * 除非我们还需要对 TomcatServletWebServerFactory 内的部分方法进行覆盖, 那就需要去 new 了
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizernew = new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {

            public void customize(TomcatServletWebServerFactory factory) {
                Collection<TomcatConnectorCustomizer> list = factory.getTomcatConnectorCustomizers();
                for (TomcatConnectorCustomizer tomcatConnectorCustomizer : list) {
                    Class<?> clazz = tomcatConnectorCustomizer.getClass();
                    System.out.println(clazz);
                }
                factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                    public void customize(Connector connector) {
                        connector.setPort(9098);
                        connector.setSecure(true);
                        connector.setScheme("https");

                        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                        // pom中不指定resources的话，就需要写成 classpath:key/website.ks
                        protocol.setKeystoreFile("key/website.ks");
                        protocol.setKeyPass(KEYPASS);      // Admin@1234
                        protocol.setKeystorePass(KEYSTOREPASS); // Admin@123
                        protocol.setKeystoreType("JKS");
                        protocol.setSSLEnabled(true);
                        protocol.setClientAuth("false");
                    }
                });
            }
        };
        return webServerFactoryCustomizernew;
    }
}
