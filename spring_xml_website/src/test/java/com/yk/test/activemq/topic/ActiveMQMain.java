package com.yk.test.activemq.topic;

import com.yk.demo.activemq.demo.general.GeneralExample;
import com.yk.demo.activemq.demo.topic.TopicConsumer;
import com.yk.demo.activemq.demo.topic.TopicProducer;
import org.apache.activemq.broker.BrokerService;

public class ActiveMQMain {

    public static void main(String args[]) throws Exception {
        BrokerService service = new BrokerService();

        service.addConnector("tcp://127.0.0.1:61616");
        service.setUseJmx(true);
        // 设置broker名字
        service.setBrokerName("MyBroker");
        // 是否使用持久化
        service.setPersistent(false);

        service.start();


        new TopicConsumer();
        TopicProducer producer = new TopicProducer();
        while (true)
        {
            producer.publishMessages();
            Thread.sleep(1000);
        }
    }
}
