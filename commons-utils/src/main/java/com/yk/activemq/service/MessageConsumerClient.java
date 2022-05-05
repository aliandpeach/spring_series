package com.yk.activemq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

/**
 * 消息消费客户端
 */
public class MessageConsumerClient extends Client
{

    private static final Logger logger = LoggerFactory.getLogger("activemq");

    @Override
    public void setListener(MessageListener listener)
    {
        try
        {
            subscriber.setMessageListener(listener);
        }
        catch (JMSException e)
        {
            logger.error("activemq Consumer setListener error ", e);
            throw new RuntimeException("activemq Consumer setListener error ", e);
        }
    }
}
