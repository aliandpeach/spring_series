package com.yk.demo.event.demo;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 路由， 实际的消费者，通过分发消息到各个订阅者
 *
 * 订阅者（实际业务类）不必再每个去实现DemoApplicationListener接口，否则都需要交给DemoApplicationContext管理
 * 这样基础功能就不需要再关注业务
 */
public class DemoEventConsumerProxy implements DemoApplicationListener {

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private Map<MessageTopic, BlockingQueue<MessageTaskManager>> proxys = new ConcurrentHashMap<>();

    public DemoEventConsumerProxy() {
    }

    public synchronized void addSubscribes(MessageTaskManager task) {
        BlockingQueue queue = proxys.get(task.getMessageTopic());
        if (queue == null) {
            proxys.put(task.getMessageTopic(), new LinkedBlockingQueue<>());
        }
        try {
            proxys.get(task.getMessageTopic()).put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException("addSubscribes error", e);
        }
    }

    public synchronized void delSubscribes(MessageTaskManager task) {
        BlockingQueue queue = proxys.get(task.getMessageTopic());
        if (queue == null) {
            proxys.put(task.getMessageTopic(), new LinkedBlockingQueue<>());
            return;
        }
        try {
            proxys.get(task.getMessageTopic()).remove(task);
        } catch (Exception e) {
            throw new RuntimeException("delSubscribes error", e);
        }
    }

    /*static {
        DemoEventConsumerProxy proxy = new DemoEventConsumerProxy();
        DemoApplicationContext.getInstance().addApplicationListener(proxy);

        for (MessageTaskManager task : MessageCenter.allSubscribers) {
            proxy.addSubscribes(task);
        }
    }*/

    @Override
    public void onApplicationEvent(DemoApplicationEvent e) {
        Runnable runnable = () -> {
            MessageForm form = e.getMessageForm();
            BlockingQueue<MessageTaskManager> queue = proxys.get(form.getMessageTopic());
            if (null == queue) {
                return;
            }
            for (MessageTaskManager task : queue) {
                task.readyMessageHandleThread(e.getMessageForm());
            }
        };
        executor.submit(runnable);
    }
}
