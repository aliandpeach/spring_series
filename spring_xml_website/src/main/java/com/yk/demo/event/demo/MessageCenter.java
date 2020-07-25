package com.yk.demo.event.demo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.ServiceLoader;

@WebListener
public class MessageCenter implements ServletContextListener {

    private static DemoEventConsumerProxy proxy = new DemoEventConsumerProxy();

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        /**
         * 订阅者的两种订阅方式，
         * 1、继承MessageTaskManager，实现抽象方法。
         * 2、作为Spring Bean的情况可以在bean初始化完成后，调用MessageCenter.getProxy().addSubscribes
         *    或者在com.yk.demo.event.demo.MessageTaskManager写入MessageTaskManager实现类的全类限定名，就可以被自动加载
         */
        ServiceLoader<MessageTaskManager> loader = ServiceLoader.<MessageTaskManager>load(MessageTaskManager.class);
        Iterator<MessageTaskManager> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageTaskManager service = iterator.next();
            if (service.getClass().isInterface() || Modifier.isAbstract(service.getClass().getModifiers())) {
                continue;
            }
            proxy.addSubscribes(service);
        }

        /*try {
         *//**
         * 此处使用Class.forName加载全类限定名，初始化类DemoEventConsumerProxy
         *
         * 此方式加载和创建JDBC连接时加载Driver的方式一样
         *
         * Class.forName能够加载类到JVM中（即把类后编译后的二进制加载到Class对象中）。且执行类中的static块，ClassLoader不能执行static
         *//*
            Class.forName("com.yk.demo.event.demo.DemoEventConsumerProxy");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("newInstance DemoEventConsumerProxy error", e);
        }*/

        DemoApplicationContext.getInstance().addApplicationListener(proxy);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    public static DemoEventConsumerProxy getProxy() {
        return proxy;
    }
}
