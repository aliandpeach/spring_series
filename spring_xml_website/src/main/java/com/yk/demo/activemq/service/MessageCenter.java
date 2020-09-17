package com.yk.demo.activemq.service.sdk;

import com.yk.demo.activemq.service.Client;
import com.yk.demo.activemq.service.MessageConsumerClient;
import com.yk.demo.activemq.service.MessageForm;
import com.yk.demo.activemq.service.MessageListenerProxy;
import com.yk.demo.activemq.service.MessageProducerClient;
import com.yk.demo.activemq.service.MessageTaskManager;
import com.yk.demo.activemq.service.MessageTopic;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageCenter {

    private Logger logger = LoggerFactory.getLogger("activemq");

    /**
     * 根据Topic存储多个生产客户端
     */
    private Map<MessageTopic, Client> producers = new ConcurrentHashMap<>();
    /**
     * 根据Topic存储多个消费客户端
     */
    private Map<MessageTopic, Client> consumers = new ConcurrentHashMap<>();

    /**
     * 存储多个订阅者转发类
     */
    private Map<MessageTopic, MessageListenerProxy> listenerProxys = new ConcurrentHashMap<>();

    private static String tcpurl = "tcp://127.0.0.1:61616";

    /**
     * 是否启动内置ACTIVEMQ服务器以及 是否启动消费者客户端还是启动生产者客户端
     *
     * @param init 是否启动内置ACTIVEMQ服务器
     * @param type 是否启动消费者客户端还是启动生产者客户端 0 都启动 1 启动生产者 2 启动消费者
     */
    private MessageCenter(boolean init, int type){
        initBroker(init);

        for (MessageTopic topic : MessageTopic.values()) {

            if (type == 1 || type == 0) {
                Client producer = new MessageProducerClient();
                producer.connect(tcpurl, topic.name());
                producers.put(topic, producer);
            }
            if (type == 2 || type == 0) {
                MessageListenerProxy proxy = new MessageListenerProxy();
                listenerProxys.put(topic, proxy);
                Client consumer = new MessageConsumerClient();
                consumer.connect(tcpurl, topic.name());
                consumer.setListener(proxy);
                consumers.put(topic, consumer);
            }
        }
    }

    private void initBroker(boolean init){
        if (!init) {
            return;
        }
        try {
            BrokerService service = new BrokerService();
            service.addConnector(tcpurl);
            service.setUseJmx(true);
            // 设置broker名字
            service.setBrokerName("MessageCenterService");
            // 是否使用持久化
            service.setPersistent(false);
            service.start();
        } catch (Exception e) {
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
    public static MessageCenter newInstance(boolean initBroker, int type) throws RuntimeException {
        int t = Optional.of(type).filter(e -> e <= 2 && e >= 0).orElseThrow(() -> new RuntimeException("type is not correct 0 or 1 or 2 is required"));
        return new MessageCenter(initBroker, t);
    }

    /**
     * sdk provider : register
     * @param task
     */
    public synchronized void addSubscribes(MessageTaskManager task) {
        MessageTopic topic = task.getTopic();
        Optional.ofNullable(listenerProxys.get(topic)).ifPresent(t -> t.addSubscribes(task));
    }

    /**
     * sdk provider : unregister
     *
     * @param task
     */
    public synchronized void delSubscribes(MessageTaskManager task) {
        MessageTopic topic = task.getTopic();
        Optional.ofNullable(listenerProxys.get(topic)).ifPresent(t -> t.delSubscribes(task));
    }

    /**
     * sdk provider : send
     *
     * @param messageForm
     */
    public synchronized void sendMessage(MessageForm messageForm) {
        Optional.ofNullable(messageForm).map(t -> t.getMessageTopic()).orElseThrow(() -> new RuntimeException("sendMessage error"));
        producers.get(messageForm.getMessageTopic()).sendMessage(messageForm);
    }
}
