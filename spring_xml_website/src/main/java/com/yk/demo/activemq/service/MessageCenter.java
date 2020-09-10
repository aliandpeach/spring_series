package com.yk.demo.activemq.service.sdk;

import com.yk.demo.activemq.service.MessageClient;
import com.yk.demo.activemq.service.MessageTopic;
import com.yk.demo.activemq.service.SubscribeProxy;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jmx.JmxException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCenter {

    /**
     * 根据Topic存储多个客户端
     */
    private Map<MessageTopic, MessageClient> clients = new ConcurrentHashMap<>();

    /**
     * 存储多个订阅者转发类
     */
    private Map<MessageTopic, SubscribeProxy> subscribers = new ConcurrentHashMap<>();

    private MessageCenter() throws Exception {
        initBroker();
    }

    private void initBroker() throws Exception {
        BrokerService service = new BrokerService();
        service.addConnector("tcp://127.0.0.1:61616");
        service.setUseJmx(true);
        // 设置broker名字
        service.setBrokerName("MessageCenterService");
        // 是否使用持久化
        service.setPersistent(false);
        service.start();
    }
}
