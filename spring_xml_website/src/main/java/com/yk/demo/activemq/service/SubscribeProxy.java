package com.yk.demo.activemq.service;


import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 订阅者的转发类， 由该类进行订阅后，转发至其他订阅者
 */
public class SubscribeProxy implements MessageListener {

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    /**
     * 存储所有的订阅者，订阅者都继承于MessageTaskManager
     */
    private Map<MessageTopic, BlockingQueue<MessageTaskManager>> all = new ConcurrentHashMap<>();

    public synchronized void addSubscribes(MessageTaskManager task) {
        BlockingQueue queue = all.get(task.getTopic());
        if (queue == null) {
            all.put(task.getTopic(), new LinkedBlockingQueue<>());
        }
        try {
            all.get(task.getTopic()).put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException("SubscribeProxy : addSubscribes error", e);
        }
    }

    public synchronized void delSubscribes(MessageTaskManager task) {
        BlockingQueue queue = all.get(task.getTopic());
        if (queue == null) {
            all.put(task.getTopic(), new LinkedBlockingQueue<>());
            return;
        }
        try {
            all.get(task.getTopic()).remove(task);
        } catch (Exception e) {
            throw new RuntimeException("delSubscribes error", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        Runnable runnable = () -> {
            BytesMessage bytesMessage = (BytesMessage) message;

            MessageForm form = null;
            int len;
            byte[] buffer = new byte[1024 * 1024];
            try {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                /*ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject();*/


                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while ((len = bytesMessage.readBytes(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }

                bytesMessage.reset();
                while ((len = bytesMessage.readBytes(buffer)) != -1) {
                    byteBuffer.flip();
                    byteBuffer.put(buffer, 0, len);
                }

                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object obj = objectInputStream.readObject();
            } catch (JMSException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            BlockingQueue<MessageTaskManager> queue = all.get(form.getMessageTopic());
            if (null == queue) {
                return;
            }
            for (MessageTaskManager task : queue) {
                task.onMessageTask(form);
            }
        };
        executor.submit(runnable);
    }
}
