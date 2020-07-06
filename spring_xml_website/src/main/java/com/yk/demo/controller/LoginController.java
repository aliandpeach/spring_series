package com.yk.demo.controller;

import com.yk.demo.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.ServletContextResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger("demo");


    @PostConstruct
    public void init() throws IOException {
        logger.info("LoginController init...");
        ClassPathResource env = new ClassPathResource("config/env.properties");

        /*
        Thread.currentThread().getClass().getClassLoader().getResource("").getFile();
        Thread.currentThread().getClass().getResource("");
        ClassLoader.getSystemResources("");
        */

        /*
        new FileSystemResource("D:\xxx\xxx.properties").getInputStream();
        new ClassPathResource("config/env.properties").getInputStream();
        new ServletContextResource(servletContext, "").getInputStream();
        */

        /*
        EncodedResource encodedResource = new EncodedResource(new ClassPathResource(""),"UTF-8");
        FileCopyUtils.copyToString(encodedResource.getReader());
        */

        /**
         * 资源加载
         */

//        Resource[] resource = new PathMatchingResourcePatternResolver().getResources("classpath*:com/**/*.xml");
//        Resource resource = new PathMatchingResourcePatternResolver().getResource("classpath*:com/**/*.xml");
//        resource.getInputStream();

        /*
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new PathMatchingResourcePatternResolver().getResources(""));
        Car car = factory.getBean(Car.class);
        */
    }
}
