package com.yk.demo.event.demo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@WebListener
public class MessageCenter implements ServletContextListener
{

    private List<String> topics = new CopyOnWriteArrayList<>();

    private static Map<String, EventConsumerProxy> proxyMap = new ConcurrentHashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        /**
         * 初始化 EventListener
         */
        topics.addAll(Arrays.stream(EventType.values()).map(Enum::name).collect(Collectors.toList()));
        topics.forEach(topic ->
        {
            EventConsumerProxy proxy = new EventConsumerProxy(topic);
            ApplicationContext.getInstance().addApplicationListener(proxy);
            proxyMap.put(topic, proxy);
        });

        /**
         * 订阅者的两种订阅方式，
         * 1、继承MessageTaskManager，实现抽象方法。
         * 2、作为Spring Bean的情况可以在bean初始化完成后，调用MessageCenter.getProxy().addSubscribes
         *    或者在com.yk.demo.event.demo.MessageTaskManager写入MessageTaskManager实现类的全类限定名，就可以被自动加载
         */
        ServiceLoader<MessageTaskManager> loader = ServiceLoader.<MessageTaskManager>load(MessageTaskManager.class);
        Iterator<MessageTaskManager> iterator = loader.iterator();
        while (iterator.hasNext())
        {
            MessageTaskManager manager = iterator.next();
            if (manager.getClass().isInterface() || Modifier.isAbstract(manager.getClass().getModifiers()))
            {
                continue;
            }
            addSubscribes(manager);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }

    public static void addSubscribes(MessageTaskManager manager)
    {
        proxyMap.get(manager.getTopic()).addSubscribes(manager);
    }

    public static void delSubscribes(MessageTaskManager manager)
    {
        proxyMap.get(manager.getTopic()).delSubscribes(manager);
    }
}
