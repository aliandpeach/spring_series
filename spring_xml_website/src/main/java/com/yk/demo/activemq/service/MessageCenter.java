package com.yk.demo.activemq.service;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCenter<T>
{

    private Logger logger = LoggerFactory.getLogger("activemq");

    private MessageCenter<T> INSTANCE;

    /**
     * 一个Topic对应初始化一个生产客户端
     */
    private final Map<MessageTopic, Client<T>> producers = new ConcurrentHashMap<>();
    /**
     * 一个Topic对应初始化一个订阅客户端
     */
    private final Map<MessageTopic, Client<T>> consumers = new ConcurrentHashMap<>();

    /**
     * 一个Topic对应一个订阅端 （ javax.jms.MessageListener）
     */
    private final Map<MessageTopic, MessageListenerProxy<T>> listenerProxys = new ConcurrentHashMap<>();

    private static final String TCP_URL = "tcp://127.0.0.1:61616";

    /**
     * 是否启动内置ACTIVEMQ服务器以及 是否启动消费者客户端还是启动生产者客户端
     *
     * @param init 是否启动内置ACTIVEMQ服务器
     * @param type 是否启动消费者客户端还是启动生产者客户端 0 都启动 1 启动生产者 2 启动消费者
     */
    private MessageCenter(boolean init, int type)
    {
        initBroker(init);

        for (MessageTopic topic : MessageTopic.values())
        {

            if (type == 1 || type == 0)
            {
                Client<T> producer = new MessageProducerClient<T>();
                producer.connect(TCP_URL, topic.name());
                producers.put(topic, producer);
            }
            if (type == 2 || type == 0)
            {
                MessageListenerProxy<T> proxy = new MessageListenerProxy<>();
                listenerProxys.put(topic, proxy);
                Client<T> client = new MessageConsumerClient<T>();
                client.connect(TCP_URL, topic.name());
                client.setListener(proxy);
                consumers.put(topic, client);
            }
        }
    }

    private void initBroker(boolean init)
    {
        if (!init)
        {
            return;
        }
        try
        {
            BrokerService service = new BrokerService();
            service.addConnector(TCP_URL);
            service.setUseJmx(true);
            // 设置broker名字
            service.setBrokerName("MessageCenterService");
            // 是否使用持久化
            service.setPersistent(false);
            service.start();
        }
        catch (Exception e)
        {
            logger.error("MessageCenterService init failed", e);
            throw new RuntimeException("MessageCenterService init failed", e);
        }
    }

    /**
     * sdk provider : init
     *
     * @param initBroker
     * @param type
     * @return
     * @throws RuntimeException
     */
    public synchronized static <T> MessageCenter<T> newInstance(boolean initBroker, int type) throws RuntimeException
    {
        int t = Optional.of(type).filter(e -> e <= 2 && e >= 0).orElseThrow(() -> new RuntimeException("type is not correct 0 or 1 or 2 is required"));
        return new MessageCenter<>(initBroker, t);
    }

    /**
     * sdk provider : register
     *
     * @param task
     */
    public synchronized void addSubscribes(MessageTaskManager<T> task)
    {
        MessageTopic topic = task.getTopic();
        Optional.ofNullable(listenerProxys.get(topic)).ifPresent(t -> t.addSubscribes(task));
    }

    /**
     * sdk provider : unregister
     *
     * @param task
     */
    public synchronized void delSubscribes(MessageTaskManager<T> task)
    {
        MessageTopic topic = task.getTopic();
        Optional.ofNullable(listenerProxys.get(topic)).ifPresent(t -> t.delSubscribes(task));
    }

    /**
     * sdk provider : send
     *
     * @param messageForm
     */
    public synchronized void sendMessage(MessageForm<T> messageForm)
    {
        Optional.ofNullable(messageForm).map(MessageForm::getMessageTopic).orElseThrow(() -> new RuntimeException("sendMessage error"));
        producers.get(messageForm.getMessageTopic()).sendMessage(messageForm);
    }
}
