package com.yk.test.activemq.topic;

import com.yk.activemq.service.MessageCenter;
import com.yk.activemq.service.MessageForm;
import com.yk.activemq.service.MessageTaskManager;
import com.yk.activemq.service.MessageTopic;
import com.yk.demo.activemq.demo.topic.TopicConsumer;
import com.yk.demo.activemq.demo.topic.TopicConsumer2;
import com.yk.demo.activemq.demo.topic.TopicProducer;
import com.yk.demo.event.demo.ApplicationContext;
import com.yk.demo.event.demo.ApplicationEvent;
import com.yk.demo.event.demo.EventType;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ActiveMQMainTopic
{

    public static void main(String args[]) throws Exception
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        BrokerService service = new BrokerService();

        service.addConnector("tcp://127.0.0.1:61616");
        service.setUseJmx(true);
        // 设置broker名字
        service.setBrokerName("MyBroker");
        // 是否使用持久化
        service.setPersistent(false);

        service.start();


        new TopicConsumer();
        new TopicConsumer2();
        TopicProducer producer = new TopicProducer();
        producer.publishMessages();
        Thread.sleep(1000);
    }

    @Test
    public void testMQ() throws InterruptedException
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        MessageCenter center = MessageCenter.newInstance(true, 0);
        center.addSubscribes(new MessageTaskManager()
        {
            @Override
            public void onMessageTask(MessageForm messageForm)
            {
                Assert.assertTrue(true);
            }

            @Override
            public void replay(Session session, Message message)
            {
                if (null == session)
                {
                    return;
                }
                try
                {
                    TextMessage replayText = session.createTextMessage("replay..." + MessageTopic.MODIFY.name());
                    MessageProducer replay = session.createProducer(message.getJMSReplyTo());
                    replay.send(replayText);
                }
                catch (JMSException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.MODIFY;
            }
        });
        center.addSubscribes(new MessageTaskManager()
        {
            @Override
            public void onMessageTask(MessageForm messageForm)
            {
                Assert.assertTrue(true);
            }

            @Override
            public void replay(Session session, Message message)
            {
                if (null == session)
                {
                    return;
                }

                try
                {
                    TextMessage replayText = session.createTextMessage("replay..." + MessageTopic.ADD.name());
                    MessageProducer replay = session.createProducer(message.getJMSReplyTo());
                    replay.send(replayText);
                }
                catch (JMSException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.ADD;
            }
        });

        MessageForm form = new MessageForm().ofTopic(MessageTopic.ADD);
        form.setSource(new HashMap<>(Collections.singletonMap("info", "success")));
        String replay = center.sendMessage(form);
        System.out.println(replay);

        Semaphore semaphore = new Semaphore(1);
        Runnable toRelease = () ->
        {
        };
        semaphore.acquire();
        toRelease = semaphore::release;
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
