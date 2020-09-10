package com.yk.demo.activemq.service;

public abstract class MessageTaskManager {

    protected abstract void onMessageTask(MessageForm messageForm);

    protected abstract MessageTopic getTopic();
}
