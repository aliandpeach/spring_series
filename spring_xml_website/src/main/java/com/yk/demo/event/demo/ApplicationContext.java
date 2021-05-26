package com.yk.demo.event.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext
{
    /**
     * 存放所有的监听器
     */
    private Map<String, ApplicationListener> listeners;

    private ApplicationContext()
    {
        this.listeners = new ConcurrentHashMap<>();
    }

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    public void addApplicationListener(ApplicationListener listener)
    {
        this.listeners.put(listener.getEventType(), listener);
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
        listeners.get(event.getEventType()).onApplicationEvent(event);
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
