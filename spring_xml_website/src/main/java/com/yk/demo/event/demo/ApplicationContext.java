package com.yk.demo.event.demo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationContext
{
    private final ExecutorService executor = Executors.newFixedThreadPool(30);

    /**
     * 存放所有的监听器
     */
    private final Map<String, ApplicationListener> listeners;

    private ApplicationContext()
    {
        this.listeners = new ConcurrentHashMap<>();
    }

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    public ApplicationListener addApplicationListener(ApplicationListener listener)
    {
        // 有就不插入数据, 返回已有数据
        return this.listeners.computeIfAbsent(listener.getEventType(), t -> listener);
    }

    /**
     * 发布事件
     * 回调所有监听器的回调方法
     *
     * @param event 事件
     */
    public void publishEvent(ApplicationEvent event)
    {
        /**
         * CopyOnWriteArrayList 可以安全的进行遍历，不必担心遍历过程中其他线程的add/remove操作
         */
//        for (Map.Entry<String, ApplicationListener> listenerEntry : listeners.entrySet())
//        {
//            if (!listenerEntry.getKey().equalsIgnoreCase(event.getEventType()))
//            {
//                continue;
//            }
//            listenerEntry.getValue().onApplicationEvent(event);
//        }
        CompletableFuture.runAsync(() -> listeners.computeIfAbsent(event.getEventType(), t -> new EventConsumerProxy(event.getEventType())).onApplicationEvent(event), executor);
    }

    public static ApplicationContext getInstance()
    {
        return ApplicationContextHolder.INSTANCE;
    }

    private static class ApplicationContextHolder
    {
        public static ApplicationContext INSTANCE = new ApplicationContext();
    }
}
