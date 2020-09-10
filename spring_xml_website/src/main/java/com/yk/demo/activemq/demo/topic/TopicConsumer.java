package com.yk.demo.activemq.demo.topic;

import lombok.SneakyThrows;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.util.Optional;

public class TopicConsumer {
    private Logger logger = LoggerFactory.getLogger("activemq");

    private TopicConnectionFactory factory = null;

    private TopicConnection connection;

    private TopicSession session;

    private TopicSubscriber subscriber;

    public TopicConsumer() {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try {
            connection = factory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("Topic.A.topic");
            subscriber = session.createSubscriber(topic);

            subscriber.setMessageListener(new MessageListener() {
                @SneakyThrows
                @Override
                public void onMessage(Message message) {
                    BytesMessage bytesMessage = (BytesMessage) message;
                    byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(buffer);
                    System.out.println(new String(buffer, 0, (int)bytesMessage.getBodyLength()));
                }
            });
        } catch (JMSException e) {
            logger.error("GeneralExample init error", e);
        }
    }
}
