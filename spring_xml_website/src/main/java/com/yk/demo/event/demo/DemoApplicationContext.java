package com.yk.demo.event.demo;

import java.util.concurrent.CopyOnWriteArrayList;

public class DemoApplicationContext {

    /**
     * 存放所有的监听器
     */
    private CopyOnWriteArrayList<DemoApplicationListener> listeners;

    private DemoApplicationContext() {
        this.listeners = new CopyOnWriteArrayList<>();
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
        /**
         * CopyOnWriteArrayList 可以安全的进行遍历，不必担心遍历过程中其他线程的add/remove操作
         */
        for (DemoApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    public static DemoApplicationContext getInstance() {
        return DemoApplicationContextHolder.INSTANCE;
    }

    private static class DemoApplicationContextHolder {
        public static DemoApplicationContext INSTANCE = new DemoApplicationContext();
    }
}
