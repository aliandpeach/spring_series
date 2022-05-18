package com.yk.mq;

import com.yk.core.CommonInfo;
import com.yk.event.InitializingEvent;
import com.yk.event.InitializingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ActiveMQ初始化中心类
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 09:56
 */
public class MessageCenter implements InitializingListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCenter.class);

    private boolean init;

    private static MessageCenter INSTANCE;

    public static MessageCenter get()
    {
        if (null == INSTANCE)
        {
            synchronized (MessageCenter.class)
            {
                if (null == INSTANCE)
                {
                    INSTANCE = new MessageCenter();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 订阅主题
     */
    public static final String TOPIC = "test-response";

    /**
     * 一个Topic对应一个消费客户端
     */
    private Map<String, Client> consumers = new ConcurrentHashMap<>();

    /**
     * 一个Topic对应一个客户端的路由转发
     */
    private final Map<String, MessageListenerProxy> listenerProxys = new ConcurrentHashMap<>();

    /**
     * 初始化订阅消费者
     */
    private MessageCenter()
    {
    }

    private synchronized void init(CommonInfo info)
    {
        MessageListenerProxy proxy = listenerProxys.computeIfAbsent(TOPIC, t -> new MessageListenerProxy(TOPIC));
        LOGGER.info(proxy.toString());
        Client consumer = consumers.computeIfAbsent(TOPIC, t -> new MessageConsumerClient());
        LOGGER.info(consumer.toString());
        proxy.setClient(consumer);
        consumer.connect(info, TOPIC, info.isForceReConnectActiveMQ());
        consumer.setListener(proxy);
        init = true;
    }

    /**
     * 某个ActiveMQ主题下的二级主动订阅, ActiveMQ接收某个主题消息后, 转发接收到的消息给二级订阅者
     */
    public synchronized void addSubscribes(MessageTaskManager task, String id)
    {
        Optional.ofNullable(listenerProxys.get(task.getTopic())).ifPresent(t -> t.addSubscribes(task, id));
    }

    /**
     * 删除二级订阅者
     */
    public synchronized void delSubscribes(String id)
    {
        Optional.ofNullable(listenerProxys.get(TOPIC)).ifPresent(t -> t.delSubscribes(id));
    }

    @Override
    public void onInitializing(InitializingEvent message)
    {
        if (!message.getInfo().isInitActivemq())
        {
            return;
        }
        if (message.getType().equalsIgnoreCase(MessageCenter.class.getName()))
        {
            get().init(message.getInfo());
        }
    }
}