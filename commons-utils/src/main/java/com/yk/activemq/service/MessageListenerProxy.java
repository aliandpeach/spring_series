package com.yk.activemq.service;

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
public class MessageListenerProxy implements MessageListener
{

    private final ExecutorService activeMQListenerExecutor = Executors.newFixedThreadPool(10);

    private final ExecutorService messageProxyExecutor = Executors.newFixedThreadPool(20);

    /**
     * 存储某个MessageTopic下 所有的业务类订阅者，订阅者都继承于MessageTaskManager
     *
     * proxy.size() 始终为 1
     */
    private Map<MessageTopic, BlockingQueue<MessageTaskManager>> proxy = new ConcurrentHashMap<>();
    /**
     * 存储MessageListener的Client
     *
     * consumer.size() 始终为 1
     */
    private Map<MessageTopic, Client> consumer = new ConcurrentHashMap<>();

    public void setConsumer(MessageTopic topic, Client client)
    {
        consumer.computeIfAbsent(topic, c -> client);
    }

    public synchronized void addSubscribes(MessageTaskManager task)
    {
        proxy.computeIfAbsent(task.getTopic(), t -> new LinkedBlockingQueue<>()).offer(task);
    }

    public synchronized void delSubscribes(MessageTaskManager task)
    {
        proxy.computeIfAbsent(task.getTopic(), t -> new LinkedBlockingQueue<>()).remove(task);
    }

    @Override
    public void onMessage(Message message)
    {
        BytesMessage bytesMessage = (BytesMessage) message;

        int len;
        byte[] buffer = new byte[1024 * 1024];
        byte[] copy;
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
             ObjectInputStream objectInput = new ObjectInputStream(inputStream))
        {
            MessageForm form = (MessageForm) objectInput.readObject();
            BlockingQueue<MessageTaskManager> queue = proxy.get(form.getMessageTopic());
            if (null == queue)
            {
                return;
            }
            queue.forEach(manager ->
            {
                proxyMessageTo(form, manager);
                // 该主题只需要一次回复即可 (虽然是多个业务客户端,但都是同一个topic)
                manager.replay(consumer.get(form.getMessageTopic()).getSession(), message);
            });

        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public void proxyMessageTo(MessageForm messageForm, MessageTaskManager task)
    {
        messageProxyExecutor.submit(new ProxyMessageThead(messageForm, task));
    }

    private static class ProxyMessageThead extends Thread
    {

        private MessageForm messageForm;

        private MessageTaskManager task;

        public ProxyMessageThead(MessageForm messageForm, MessageTaskManager task)
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
