package com.yk.test.activemq.topic;

import com.yk.demo.activemq.demo.topic.TopicConsumer;
import com.yk.demo.activemq.demo.topic.TopicProducer;
import com.yk.demo.activemq.service.MessageCenter;
import com.yk.demo.activemq.service.MessageForm;
import com.yk.demo.activemq.service.MessageTaskManager;
import com.yk.demo.activemq.service.MessageTopic;
import com.yk.demo.event.demo.ApplicationContext;
import com.yk.demo.event.demo.ApplicationEvent;
import com.yk.demo.event.demo.EventType;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActiveMQMain
{

    public static void main(String args[]) throws Exception
    {
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

    @Test
    public void testMQ()
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        MessageCenter<Map<String, String>> center = MessageCenter.<Map<String, String>>newInstance(true, 0);
        center.addSubscribes(new MessageTaskManager<Map<String, String>>()
        {
            @Override
            protected void onMessageTask(MessageForm<Map<String, String>> messageForm)
            {
                Assert.assertTrue(true);
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.MODIFY;
            }
        });
        center.addSubscribes(new MessageTaskManager<Map<String, String>>()
        {
            @Override
            protected void onMessageTask(MessageForm<Map<String, String>> messageForm)
            {
                Assert.assertTrue(true);
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.ADD;
            }
        });

        MessageForm<Map<String, String>> form = new MessageForm<>().ofTopic(MessageTopic.ADD);
        form.setSource(new HashMap<>(Collections.singletonMap("info", "success")));
        center.sendMessage(form);
    }

    @Test
    public void testEvent() throws InterruptedException
    {
        com.yk.demo.event.demo.MessageCenter center = new com.yk.demo.event.demo.MessageCenter();
        center.contextInitialized(null);

        com.yk.demo.event.demo.MessageCenter.addSubscribes(new com.yk.demo.event.demo.MessageTaskManager()
        {
            @Override
            protected String getTopic()
            {
                return EventType.ADD.name();
            }

            @Override
            protected void onMessage(com.yk.demo.event.demo.MessageForm form)
            {
                System.out.println(form);
            }
        });

        com.yk.demo.event.demo.MessageForm form = new com.yk.demo.event.demo.MessageForm();
        form.setSource(new HashMap<>(Collections.singletonMap("info", "success")));
        ApplicationContext.getInstance().publishEvent(new ApplicationEvent(form).ofEventType(EventType.ADD.name()));
        Thread.currentThread().join();
    }
}
