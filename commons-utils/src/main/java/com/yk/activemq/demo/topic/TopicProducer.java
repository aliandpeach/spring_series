package com.yk.activemq.demo.topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TopicProducer
{
    private Logger logger = LoggerFactory.getLogger("activemq");

    private AtomicInteger integer = new AtomicInteger();

    private TopicConnectionFactory factory = null;

    private TopicConnection connection;

    private TopicSession session;

    private TopicPublisher publisher;

    private Topic topic = null;

    TemporaryTopic temporaryTopic;
    TopicSubscriber receiver;

    public TopicProducer()
    {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try
        {
            connection = factory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("Topic.A.topic");
            publisher = session.createPublisher(topic);

            temporaryTopic = session.createTemporaryTopic();
            receiver = session.createSubscriber(temporaryTopic);
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample init error", e);
        }


    }

    public void publishMessages()
    {
        BytesMessage message = null;
        int i;
        final int NUMMSGS = 3;
        final String MSG_TEXT = new String("Here is a message");

        try
        {
            message = session.createBytesMessage();
            message.writeBytes(new String("bytes message" + integer.incrementAndGet()).getBytes());

            message.setJMSReplyTo(temporaryTopic);
            publisher.publish(topic, message);

            Message message1 = receiver.receive(20000);
            System.out.println(null != message1 ? ((TextMessage) message1).getText() : "no replay");
        }
        catch (JMSException e)
        {
        }
    }
}
