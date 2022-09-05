package com.yk.base.config;


import com.yk.base.mapper.TestInterfaceMapperService;
import com.yk.base.uitl.SpringContext;
import com.yk.demo.configuration.AppConfig;
import com.yk.demo.configuration.AppConfigImport;
import com.yk.demo.model.Car;
import com.yk.demo.model.Moto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@WebListener
public class StartUpListener implements ServletContextListener
{

    Logger logger = LoggerFactory.getLogger(StartUpListener.class);

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
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            // 带/ 从根目录位置查找，不带/, 从当前包位置查找, 该方法会先处理参数是否带/的过程，不带就找TestResource.class的全类限定名
            InputStream in1 = StartUpListener.class.getResource("/druid.properties").openStream();
            // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s1 = StartUpListener.class.getResource("/").getPath();
            // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/com/yk/base/config/
            String s2 = StartUpListener.class.getResource("").getPath();

            // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法 (web项目中，带不带/都可以，这是因为web项目获取的是相对路径)
            InputStream in2 = StartUpListener.class.getClassLoader().getResource("druid.properties").openStream();

            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s3 = StartUpListener.class.getClassLoader().getResource("/").getPath();
            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s4 = StartUpListener.class.getClassLoader().getResource("").getPath();

            // 无法获取到getClassLoader 不可用
            // InputStream in3 = Thread.currentThread().getClass().getClassLoader().getResource("druid.properties").openStream();

            // 必须带/，否则在处理不带/的参数的过程中，会走到java/lang/jdbc.properties (其实就是Thread类的全类限定名)(非web 可用)
            // InputStream in4 = Thread.currentThread().getClass().getResource("druid.properties").openStream();
            // String s5 = Thread.currentThread().getClass().getResource("/").getPath();
            // String s6 = Thread.currentThread().getClass().getResource("").getPath();

            // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法 (web项目中，带不带/都可以)
            InputStream in5 = Thread.currentThread().getContextClassLoader().getResource("druid.properties").openStream();

            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s7 = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s8 = Thread.currentThread().getContextClassLoader().getResource("").getPath();


            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s9 = this.getClass().getClassLoader().getResource("/").getPath();
            // /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s10 = this.getClass().getClassLoader().getResource("").getPath();

            // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/
            String s11 = this.getClass().getResource("/").getPath();
            // path : /D:/idea_workspace/spring_series/spring_xml_website/target/spring_xml_website/WEB-INF/classes/com/yk/base/config/
            String s12 = this.getClass().getResource("").getPath();

            Properties properties = new Properties();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ClassPathResource env = new ClassPathResource("spring/bean.xml"); //类路径的文件
        String path1 = env.getPath(); //类路径的文件
        try (InputStream in = env.getInputStream())
        {
            DataInputStream data = new DataInputStream(in);
            DataOutputStream out = new DataOutputStream(new FileOutputStream("D:\\env.txt"));
            int size = in.available();
            byte[] buf = new byte[size];
            data.readFully(buf);
            out.write(buf);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String realPath = sce.getServletContext().getRealPath("/");
        PathResource env1 = new PathResource(realPath + "WEB-INF/classes/spring/bean.xml");// WritableResource
        /*String path = env1.getPath();
        try (OutputStream out = env1.getOutputStream()) {
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ServletContextResource contextResource = new ServletContextResource(sce.getServletContext(), "/WEB-INF/classes/spring/bean.xml");
        String abPath = WebUtils.getTempDir(sce.getServletContext()).getAbsolutePath();
        try (InputStream in = contextResource.getInputStream();
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             StringWriter stringWriter = new StringWriter())
        {
            copy(stringWriter, reader);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        /*
        Resource xxx = new FileSystemResource("D:\xxx\xxx.txt").getInputStream();
        Resource env = new ClassPathResource("spring/bean.xml").getInputStream();
        Resource env1 = new ServletContextResource(servletContext, "").getInputStream();
        */

        EncodedResource encodedResource = new EncodedResource(new ClassPathResource("spring/bean.xml"), "UTF-8");
        try
        {
            String content = FileCopyUtils.copyToString(encodedResource.getReader());
//            Optional.of(content).ifPresent(System.out::println);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResource("spring/bean.xml").openStream();
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             StringWriter stringWriter = new StringWriter();)
        {
            copy(stringWriter, reader);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        /**
         * 批量资源加载
         */

//        Resource[] resource = new PathMatchingResourcePatternResolver().getResources("classpath*:com/**/*.xml");
//        Resource resource = new PathMatchingResourcePatternResolver().getResource("classpath*:com/**/*.xml");
//        resource.getInputStream();

        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        try
        {
            reader.loadBeanDefinitions(new PathMatchingResourcePatternResolver().getResources("classpath:spring/bean.xml"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        factory.addBeanPostProcessor(new MyBeanPostProcessor());
        factory.addBeanPostProcessor(new MyInstantiationAwareBeanProcessor());

        Car car1 = factory.getBean("car1", Car.class);
        Car car2 = factory.getBean("car2", Car.class);
        Car car3 = factory.getBean("car3", Car.class);

        Car car4 = factory.getBean("car4", Car.class);
        Car car5 = factory.getBean("car5", Car.class);
        Car car6 = factory.getBean("car6", Car.class);

        Moto moto = factory.getBean(Moto.class);
        List<String> utilTestList = factory.getBean("utilTestList", LinkedList.class);
        Map<String, Integer> utilTestMap = factory.getBean("utilTestMap", Map.class);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring/bean1.xml"});
        Car car7 = context.getBean("car7", Car.class);

        AnnotationConfigApplicationContext annotation = new AnnotationConfigApplicationContext();
        annotation.register(AppConfig.class);
        annotation.register(AppConfigImport.class);
        annotation.refresh();

        Car car8 = annotation.getBean("car8", Car.class);
        Moto moto5 = annotation.getBean("moto5", Moto.class);
        logger.info("start up");

        TestInterfaceMapperService mapperService = SpringContext.getInstance().getBean(TestInterfaceMapperService.class);
        mapperService.test();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

    }

    private void copy(StringWriter writer, InputStreamReader reader) throws IOException
    {
        char[] buffer = new char[4069];
        int len;
        while ((len = reader.read(buffer)) != -1)
        {
            writer.write(buffer, 0, len);
        }
        writer.flush();
        String content = writer.toString();
        Optional.of(content).ifPresent(System.out::println);
    }
}
