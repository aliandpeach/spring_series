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
public class MessageListenerProxy<T> implements MessageListener
{

    private ExecutorService activeMQListenerExecutor = Executors.newFixedThreadPool(1);

    private ExecutorService messageProxyExecutor = Executors.newFixedThreadPool(2);

    /**
     * 存储某个MessageTopic下 所有的业务类订阅者，订阅者都继承于MessageTaskManager
     *
     * proxy.size() 始终为 1
     */
    private Map<MessageTopic, BlockingQueue<MessageTaskManager<T>>> proxy = new ConcurrentHashMap<>();
    /**
     * 存储Client 用户replay
     *
     * client.size() 始终为 1
     */
    private Map<MessageTopic, Client<T>> clientMap = new ConcurrentHashMap<>();

    public void setClient(MessageTopic topic, Client<T> client)
    {
        clientMap.putIfAbsent(topic, client);
    }

    public synchronized void addSubscribes(MessageTaskManager<T> task)
    {
        BlockingQueue<MessageTaskManager<T>> queue = proxy.get(task.getTopic());
        if (queue == null)
        {
            proxy.put(task.getTopic(), new LinkedBlockingQueue<>());
        }
        proxy.get(task.getTopic()).offer(task);
    }

    public synchronized void delSubscribes(MessageTaskManager<T> task)
    {
        BlockingQueue<MessageTaskManager<T>> queue = proxy.get(task.getTopic());
        if (queue == null)
        {
            proxy.put(task.getTopic(), new LinkedBlockingQueue<>());
            return;
        }
        proxy.get(task.getTopic()).remove(task);
    }

    @Override
    public void onMessage(Message message)
    {
        Runnable runnable = () ->
        {
            BytesMessage bytesMessage = (BytesMessage) message;

            MessageForm<T> form = null;
            int len;
            byte[] buffer = new byte[1024 * 1024];
            byte[] copy = new byte[0];
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
            {
                while ((len = bytesMessage.readBytes(buffer)) != -1)
                {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                copy = deepClone(byteArrayOutputStream.toByteArray());
            }
            catch (JMSException | IOException e)
            {
                return;
            }

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(copy);
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);)
            {
                form = (MessageForm<T>) objectInputStream.readObject();
            }
            catch (Exception e)
            {
                return;
            }

            BlockingQueue<MessageTaskManager<T>> queue = proxy.get(form.getMessageTopic());
            if (null == queue)
            {
                return;
            }
            for (MessageTaskManager<T> task : queue)
            {
                proxyMessageTo(form, task);
            }

            // 该主题只需要一次回复即可 (虽然是多个业务客户端,但都是同一个topic)
            for (MessageTaskManager<T> task : queue)
            {
                try
                {
                    task.replay(clientMap.get(form.getMessageTopic()).getSession(), message);
                    break;
                }
                catch (JMSException e)
                {
                }
            }
        };
        activeMQListenerExecutor.submit(runnable);
    }

    public void proxyMessageTo(MessageForm<T> messageForm, MessageTaskManager<T> task)
    {
        messageProxyExecutor.submit(new ProxyMessageThead<>(messageForm, task));
    }

    private static class ProxyMessageThead<T> extends Thread
    {

        private MessageForm<T> messageForm;
        private MessageTaskManager<T> task;

        public ProxyMessageThead(MessageForm<T> messageForm, MessageTaskManager<T> task)
        {
            this.messageForm = messageForm;
            this.task = task;
        }

        @Override
        public void run()
        {
            task.onMessageTask(messageForm);
        }
    }

    /**
     * 复制 对象
     */
    public <T> T deepClone(T source)
    {
        T result = null;
        byte[] clone = null;
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
             ObjectOutputStream objectOut = new ObjectOutputStream(byteArrayOut);)
        {
            objectOut.writeObject(source);// 把 source 对象写入 ByteArrayOutputStream
            byte[] temp = Optional.of(byteArrayOut).map(ByteArrayOutputStream::toByteArray).orElseThrow(() -> new RuntimeException("deep clone error"));
            clone = Arrays.copyOf(temp, temp.length);

        }
        catch (Exception e)
        {
            return null;
        }

        try (ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(clone);
             ObjectInputStream objectIn = new ObjectInputStream(byteArrayIn))
        {
            result = (T) objectIn.readObject();// 把byte[] 读取为对象
        }
        catch (Exception e)
        {
            return result;
        }
        return result;
    }
}
