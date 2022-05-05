package com.yk.activemq.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

/**
 * 消息发布客户端
 */
public abstract class Client
{

    private Logger logger = LoggerFactory.getLogger("activemq");

    private TopicConnectionFactory factory;

    private TopicConnection connection;

    protected TopicSession session;

    private Topic topic;

    protected TopicPublisher publisher;

    protected TopicSubscriber subscriber;

    private boolean conn;

    public synchronized void connect(String tcpUrl, String topicName)
    {
        if (conn)
        {
            return;
        }
        try
        {
            factory = new ActiveMQConnectionFactory(tcpUrl);
            connection = factory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);

            //
            publisher = session.createPublisher(topic);
            //
            subscriber = session.createSubscriber(topic);
            conn = true;
        }
        catch (JMSException e)
        {
            logger.error(String.format("activemq connect error url=%s", tcpUrl), e);
            throw new RuntimeException(String.format("activemq connect error url=%s", tcpUrl), e);
        }
    }

    protected void setListener(MessageListener listener)
    {
        throw new RuntimeException("set listener error");
    }

    protected String sendMessage(MessageForm form)
    {
        throw new RuntimeException("send message error");
    }

    public TopicSession getSession()
    {
        return session;
    }
}
