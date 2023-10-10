package com.yk.activemq.demo.topic;

import lombok.SneakyThrows;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

/**
 * 使用TopicSubscriber 一条信息会被多个消费端同时消费
 */
public class TopicConsumer2
{
    private Logger logger = LoggerFactory.getLogger("activemq");

    private TopicConnectionFactory factory = null;

    private TopicConnection connection;

    private TopicSession session;

    private TopicSubscriber subscriber;

    MessageProducer replay;

    public TopicConsumer2()
    {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try
        {
            connection = factory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("Topic.A.topic");
            subscriber = session.createSubscriber(topic);

            subscriber.setMessageListener(new MessageListener()
            {
                @SneakyThrows
                @Override
                public void onMessage(Message message)
                {
                    BytesMessage bytesMessage = (BytesMessage) message;
                    byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(buffer);
                    System.out.println(new String(buffer, 0, (int) bytesMessage.getBodyLength()));

                    replay = session.createProducer(message.getJMSReplyTo());
                    TextMessage textMessage = session.createTextMessage("replay1...");
                    replay.send(textMessage);
                }
            });
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample init error", e);
        }
    }
}
