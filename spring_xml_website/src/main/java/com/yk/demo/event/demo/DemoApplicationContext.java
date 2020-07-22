package com.yk.demo.event.demo;

import java.util.HashSet;
import java.util.Set;

public class DemoApplicationContext {

    /**
     * 存放所有的监听器
     */
    Set<DemoApplicationListener> listeners;

    public DemoApplicationContext() {
        this.listeners = new HashSet<>();
    }

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    public void addApplicationListener(DemoApplicationListener listener) {
        this.listeners.add(listener);
    }

    /**
     * 发布事件
     * 回调所有监听器的回调方法
     *
     * @param event 事件
     */
    public void publishEvent(DemoApplicationEvent event) {
        for (DemoApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }
}
