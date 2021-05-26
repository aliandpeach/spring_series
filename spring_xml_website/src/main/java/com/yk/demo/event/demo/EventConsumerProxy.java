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

    private String topic;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private BlockingQueue<MessageTaskManager> proxys = new LinkedBlockingQueue<>();

    public EventConsumerProxy(String topic)
    {
        this.topic = topic;
    }

    public synchronized void addSubscribes(MessageTaskManager task)
    {
        if (!task.getTopic().equalsIgnoreCase(topic))
        {
            return;
        }
        proxys.offer(task);
    }

    public synchronized void delSubscribes(MessageTaskManager task)
    {
        if (!task.getTopic().equalsIgnoreCase(topic))
        {
            return;
        }
        proxys.remove(task);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent e)
    {
        Runnable runnable = () ->
        {
            MessageForm form = e.getMessageForm();
            for (MessageTaskManager task : proxys)
            {
                task.readyMessageHandleThread(form);
            }
        };
        executor.submit(runnable);
    }

    @Override
    public String getEventType()
    {
        return topic;
    }
}
