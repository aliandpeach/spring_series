package com.yk.demo.event.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 抽象出来的任务类，订阅者通过实现该抽象类方法，实现订阅
 */
public abstract class MessageTaskManager {

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    protected abstract MessageTopic getMessageTopic();

    protected abstract void onMessage(MessageForm form);

    public void readyMessageHandleThread(MessageForm form){
        MessageHandleThread thread = new MessageHandleThread(form);
        executor.submit(thread);
    }

    private class MessageHandleThread extends Thread {

        private MessageForm messageForm;

        public MessageHandleThread(MessageForm messageForm) {
            this.messageForm = messageForm;
        }

        @Override
        public void run() {
            onMessage(messageForm);
        }
    }
}
