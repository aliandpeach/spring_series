package com.yk.demo.activemq.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 业务处理类
 */
public abstract class MessageTaskManager {

    /**
     * 业务类需要实现的订阅方法
     *
     * @param messageForm messageForm
     */
    protected abstract void onMessageTask(MessageForm messageForm);

    /**
     * 业务类需要自定义的主题类型
     */
    public abstract MessageTopic getTopic();
}
