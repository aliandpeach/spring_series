package com.yk.base.config;


import org.springframework.core.io.ClassPathResource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

@WebListener
public class StartUpListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //JDK提供的方法
        /**
         * getClassLoader()是当前类加载器,而getContextClassLoader是当前线程的类加载器
         *
         * Tomcat org.apache.catalina.startup.Bootstrap启动类，启动的时候的类加载器是ClassLoader.getSystemClassLoader()。
         * 而我们后面的WEB程序，里面的jar、resources都是由Tomcat内部来加载的，所以你在代码中动态加载jar、资源文件的时候，
         * 首先应该是使用Thread.currentThread().getContextClassLoader()。如果你使用Test.class.getClassLoader()，
         * 可能会导致和当前线程所运行的类加载器不一致（因为Java天生的多线程）。
         * Test.class.getClassLoader()一般用在getResource，因为你想要获取某个资源文件的时候，这个资源文件的位置是相对固定的。
         *
         * java的类加载机制（jvm规范）是委托模型，简单的说，如果一个类加载器想要加载一个类，
         * 首先它会委托给它的parent去加载，如果它的所有parent都没有成功的加载那么它才会自己亲自来。
         *
         *
         * 如果你使用Test.class.getClassLoader()，可能会导致和当前线程所运行的类加载器不一致
         */
        // Thread.currentThread().getContextClassLoader();
        // Thread.currentThread().getClass().getClassLoader();
        // StartUpListener.class.getClassLoader();
        try {
            // 带/ 从根目录位置查找，不带/, 从当前包位置查找, 该方法会先处理参数是否带/的过程，不带就找TestResource.class的全类限定名
            InputStream in1 = StartUpListener.class.getResource("/druid.properties").openStream();
            String s1 = StartUpListener.class.getResource("/").getPath();//
            String s2 = StartUpListener.class.getResource("").getPath(); // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/com/yk/base/config/

            // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法 (web项目中，带不带/都可以，这是因为web项目获取的是相对路径)
            InputStream in2 = StartUpListener.class.getClassLoader().getResource("druid.properties").openStream();
            String s3 = StartUpListener.class.getClassLoader().getResource("/").getPath();//
            String s4 = StartUpListener.class.getClassLoader().getResource("").getPath();

            // 无法获取到getClassLoader 不可用
            // InputStream in3 = Thread.currentThread().getClass().getClassLoader().getResource("druid.properties").openStream();

            // 必须带/，否则在处理不带/的参数的过程中，会走到java/lang/jdbc.properties (其实就是Thread类的全类限定名)(非web 可用)
            // InputStream in4 = Thread.currentThread().getClass().getResource("druid.properties").openStream();
            // String s5 = Thread.currentThread().getClass().getResource("/").getPath();
            // String s6 = Thread.currentThread().getClass().getResource("").getPath();

            // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法 (web项目中，带不带/都可以)
            InputStream in5 = Thread.currentThread().getContextClassLoader().getResource("druid.properties").openStream();
            String s7 = Thread.currentThread().getContextClassLoader().getResource("/").getPath();//
            String s8 = Thread.currentThread().getContextClassLoader().getResource("").getPath();

            String s9 = this.getClass().getClassLoader().getResource("/").getPath();//
            String s10 = this.getClass().getClassLoader().getResource("").getPath();

            String s11 = this.getClass().getResource("/").getPath();//
            String s12 = this.getClass().getResource("").getPath(); // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/com/yk/base/config/

            Properties properties = new Properties();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Properties pro = new Properties();
        try {
            InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResource("/config/env.properties").openStream();
            pro.load(inputStream1);
            InputStream inputStream2 = StartUpListener.class.getClassLoader().getResource("/config/env.properties").openStream();
            pro.load(inputStream2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream3 = StartUpListener.class.getResource("/config/env.properties").openStream();
            pro.load(inputStream3);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ClassPathResource env = new ClassPathResource("config/env.properties");

        /*
        Thread.currentThread().getClass().getClassLoader().getResource("").getFile();
        Thread.currentThread().getClass().getResource("");
        ClassLoader.getSystemResources("");
        */

        /*
        Resource xxx = new FileSystemResource("D:\xxx\xxx.properties").getInputStream();
        Resource env = new ClassPathResource("config/env.properties").getInputStream();
        Resource env1 = new ServletContextResource(servletContext, "").getInputStream();
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
        BeanFactory factory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new PathMatchingResourcePatternResolver().getResources(""));
        Car car = factory.getBean(Car.class);
        factory.addBeanPostProcessor(new MyBeanPostProcessor());
        */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
