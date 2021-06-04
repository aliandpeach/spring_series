package com.yk.demo.activemq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Optional;

/**
 * 消息发布客户端
 */
public class MessageProducerClient<T> extends Client<T>
{

    private static final Logger logger = LoggerFactory.getLogger("activemq");

    //    protected TemporaryQueue temporaryQueue;
    private TemporaryTopic temporaryTopic;
    private TopicSubscriber replaySubscriber;

    @Override
    public String sendMessage(MessageForm<T> form)
    {
        Optional.ofNullable(form).map(MessageForm::getSource).orElseThrow(() -> new RuntimeException(""));
        try (ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bytesout);)
        {

            out.writeObject(form);
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(bytesout.toByteArray());
            // temporaryQueue = session.createTemporaryQueue();
            temporaryTopic = session.createTemporaryTopic();
            replaySubscriber = session.createSubscriber(temporaryTopic);
            bytesMessage.setJMSReplyTo(temporaryTopic);
            publisher.publish(publisher.getTopic(), bytesMessage);
            Message message = replaySubscriber.receive();
            if (null != message && message instanceof TextMessage)
            {
                return ((TextMessage)message).getText();
            }
            return null;
        }
        catch (JMSException e)
        {
            logger.error("ProducerClient sendMessage failed", e);
            throw new RuntimeException("ProducerClient sendMessage failed", e);
        }
        catch (IOException e)
        {
            logger.error("ProducerClient sendMessage IO error", e);
            throw new RuntimeException("ProducerClient sendMessage IO error", e);
        }
    }
}
