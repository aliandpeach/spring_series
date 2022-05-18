package com.yk.mq;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ActiveMQ消费客户端的路由转发- 该类为客户端消费信息的Listener, 储存业务订阅的信息
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 09:56
 */
public class MessageListenerProxy implements MessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerProxy.class);

    private String id = UUID.randomUUID().toString().replace("-", "");

    private ExecutorService messageProxyExecutor = Executors.newFixedThreadPool(30);

    private String topic;

    private Client client;

    public MessageListenerProxy(String topic)
    {
        this.topic = topic;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    /**
     * 存储 所有MessageListenerProxy 要转发的二级订阅者 都继承于MessageTaskManager
     */
    private final Map<String, MessageTaskManager> allTask = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message)
    {
        LOGGER.info("on message = {}", message.toString());
        TextMessage textMessage = (TextMessage) message;
        String text = "{}";
        try
        {
            text = textMessage.getText();
        }
        catch (JMSException e)
        {
            LOGGER.error("on receive text message error", e);
            return;
        }

        Map<String, String> result = JSONUtil.toBean(text, new TypeReference<Map<String, String>>()
        {
        }, true);

        if (null == result || result.size() == 0)
        {
            LOGGER.debug("result is null, message = {}", text);
            return;
        }

        if (allTask.size() == 0)
        {
            LOGGER.info("with null manager, " + allTask.size());
            LOGGER.debug("manager is null, message = {}", result);
            return;
        }
        allTask.forEach((key, manager) ->
        {
            proxyMessageTo(result, manager);
            LOGGER.info("proxy message = {}", result);
            manager.replay(message, client.getSession());
        });
    }

    public void proxyMessageTo(Map<String, String> messageForm, MessageTaskManager task)
    {
        messageProxyExecutor.submit(new ProxyMessageThead(messageForm, task));
    }

    public synchronized void addSubscribes(MessageTaskManager task, String id)
    {
        allTask.computeIfAbsent(id, i -> task);
        LOGGER.info("addSubscribes task size = " + allTask.size() + ", id=" + id);
    }

    public synchronized void delSubscribes(String id)
    {
        allTask.remove(id);
        LOGGER.info("delSubscribes task size = " + allTask.size());
    }

    private static class ProxyMessageThead extends Thread
    {

        private Map<String, String> messageForm;

        private MessageTaskManager task;

        public ProxyMessageThead(Map<String, String> messageForm, MessageTaskManager task)
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageListenerProxy that = (MessageListenerProxy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    @Override
    public String toString()
    {
        return "MessageListenerProxy{" +
                "id='" + id + '\'' +
                '}';
    }
}
