package com.yk.test.activemq.topic;

import com.yk.activemq.service.MessageCenter;
import com.yk.activemq.service.MessageForm;
import com.yk.activemq.service.MessageTaskManager;
import com.yk.activemq.service.MessageTopic;
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

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/05/06 10:31:23
 */
public class ActivemqTopicTest
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


        new com.yk.demo.activemq.demo.topic.TopicConsumer();
        new com.yk.demo.activemq.demo.topic.TopicConsumer2();
        com.yk.demo.activemq.demo.topic.TopicProducer producer = new com.yk.demo.activemq.demo.topic.TopicProducer();
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
}
