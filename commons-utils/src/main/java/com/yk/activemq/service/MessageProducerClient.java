package com.yk.activemq.service;

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
public class MessageProducerClient extends Client
{
    private static final Logger logger = LoggerFactory.getLogger("activemq");

    @Override
    public String sendMessage(MessageForm form)
    {
        Optional.ofNullable(form).map(MessageForm::getSource).orElseThrow(() -> new RuntimeException(""));

        // 对象使用该方式转换为byte[], MessageForm类以及MessageForm的source类 必须在生产端和消费端都存在, 且全限定名需要一致, 且序列化ID也要一致
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(output))
        {

            objectOutput.writeObject(form);
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(output.toByteArray());
            // temporaryQueue = session.createTemporaryQueue();
            //    protected TemporaryQueue temporaryQueue;
            TemporaryTopic temporaryTopic = session.createTemporaryTopic();
            TopicSubscriber replaySubscriber = session.createSubscriber(temporaryTopic);
            bytesMessage.setJMSReplyTo(temporaryTopic);
            publisher.publish(publisher.getTopic(), bytesMessage);
            Message message = replaySubscriber.receive(20000);
            if (message instanceof TextMessage)
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
