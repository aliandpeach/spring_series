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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 订阅者的转发类， 由该类进行订阅后，转发至其他订阅者
 */
public class MessageListenerProxy implements MessageListener {

    private ExecutorService activeMQListenerExecutor = Executors.newFixedThreadPool(1);

    private ExecutorService messageProxyExecutor = Executors.newFixedThreadPool(2);

    /**
     * 存储所有的业务类订阅者，订阅者都继承于MessageTaskManager
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
            byte[] copy = null;
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
                /*ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject();*/
                while ((len = bytesMessage.readBytes(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }

                bytesMessage.reset();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
                while ((len = bytesMessage.readBytes(buffer)) != -1) {
                    byteBuffer.flip();
                    byteBuffer.put(buffer, 0, len);
                }
                copy = deepClone(byteArrayOutputStream.toByteArray());
            } catch (JMSException | IOException e) {
                //
            }

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(copy);
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
                form = (MessageForm) objectInputStream.readObject();
            } catch (Exception e) {
                //
            }

            BlockingQueue<MessageTaskManager> queue = all.get(form.getMessageTopic());
            if (null == queue) {
                return;
            }
            for (MessageTaskManager task : queue) {
                proxyMessageTo(form, task);
            }
        };
        activeMQListenerExecutor.submit(runnable);
    }

    public void proxyMessageTo(MessageForm messageForm, MessageTaskManager task) {
        messageProxyExecutor.submit(new ProxyMessageThead(messageForm, task));
    }

    private class ProxyMessageThead extends Thread {

        private MessageForm messageForm;
        private MessageTaskManager task;

        public ProxyMessageThead(MessageForm messageForm, MessageTaskManager task) {
            this.messageForm = messageForm;
            this.task = task;
        }

        @Override
        public void run() {
            task.onMessageTask(messageForm);
        }
    }

    /**
     * 复制 对象
     *
     * @param source
     * @param <T>
     * @return
     */
    public <T> T deepClone(T source) {
        T result = null;
        byte[] clone = null;
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
             ObjectOutputStream objectOut = new ObjectOutputStream(byteArrayOut);) {
            objectOut.writeObject(source);

            byte [] temp = Optional.ofNullable(byteArrayOut).map(t -> t.toByteArray()).orElseThrow(() -> new RuntimeException("deep clone error"));
            clone = Arrays.copyOf(temp, temp.length);

        } catch (Exception e) {
            return result;
        }

        try (ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(clone);
             ObjectInputStream objectIn = new ObjectInputStream(byteArrayIn)) {
            result = (T) objectIn.readObject();
        } catch (Exception e) {
            return result;
        }
        return result;
    }
}
