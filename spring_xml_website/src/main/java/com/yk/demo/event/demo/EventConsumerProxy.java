package com.yk.demo.event.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 路由， 实际的消费者，通过分发消息到各个订阅者
 * <p>
 * 订阅者（实际业务类）不必再每个去实现DemoApplicationListener接口，否则都需要交给DemoApplicationContext管理
 * 这样基础功能就不需要再关注业务
 */
public class EventConsumerProxy implements ApplicationListener
{

    private final String topic;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private final BlockingQueue<MessageTaskManager> managers = new LinkedBlockingQueue<>();

    public EventConsumerProxy(String topic)
    {
        this.topic = topic;
    }

    @Override
    public synchronized void addSubscribes(MessageTaskManager task)
    {
        managers.offer(task);
    }

    @Override
    public synchronized void delSubscribes(MessageTaskManager task)
    {
        managers.remove(task);
    }

    @Override
    public String getEventType()
    {
        return topic;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent e)
    {
        MessageForm form = e.getMessageForm();
        for (MessageTaskManager task : managers)
        {
            readyMessageHandleThread(task, form);
        }
    }

    private static class MessageHandleThread extends Thread
    {
        private final MessageForm messageForm;

        private final MessageTaskManager task;

        public MessageHandleThread(MessageTaskManager task, MessageForm messageForm)
        {
            this.messageForm = messageForm;
            this.task = task;
        }

        @Override
        public void run()
        {
            this.task.onMessage(messageForm);
        }
    }

    private void readyMessageHandleThread(MessageTaskManager task, MessageForm form)
    {
        MessageHandleThread thread = new MessageHandleThread(task, form);
        executor.submit(thread);
    }
}
