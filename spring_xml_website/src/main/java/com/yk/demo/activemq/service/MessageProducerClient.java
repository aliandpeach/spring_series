package com.yk.demo.activemq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

/**
 * 消息发布客户端
 */
public class MessageProducerClient extends Client {

    private Logger logger = LoggerFactory.getLogger("activemq");

    @Override
    public void sendMessage(MessageForm form) {
        Optional.ofNullable(form).map(t -> t.getSource()).orElseThrow(() -> new RuntimeException(""));
        try (ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bytesout);) {

            out.writeObject(form);
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(bytesout.toByteArray());
            publisher.publish(publisher.getTopic(), bytesMessage);
        } catch (JMSException e) {
            logger.error("ProducerClient sendMessage failed", e);
            throw new RuntimeException("ProducerClient sendMessage failed", e);
        } catch (IOException e) {
            logger.error("ProducerClient sendMessage IO error", e);
            throw new RuntimeException("ProducerClient sendMessage IO error", e);
        }
    }
}
