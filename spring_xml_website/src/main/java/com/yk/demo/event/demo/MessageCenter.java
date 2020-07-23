package com.yk.demo.event.demo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MessageCenter implements ServletContextListener {

    private static DemoEventConsumerProxy proxy = new DemoEventConsumerProxy();

    @Override
    public void contextInitialized(ServletContextEvent sce) {



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
