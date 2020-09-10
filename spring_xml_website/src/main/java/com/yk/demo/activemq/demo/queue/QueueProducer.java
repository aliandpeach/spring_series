package com.yk.demo.activemq.demo.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

public class QueueProducer {
    private Logger logger = LoggerFactory.getLogger("activemq");

    private QueueConnectionFactory factory = null;

    private QueueConnection connection;

    private QueueSession session;

    private QueueSender sender;

    private Queue queue = null;

    public QueueProducer() {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try {
            connection = factory.createQueueConnection();
            connection.start();
            session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue("Queue.A.queue");
            sender = session.createSender(queue);


        } catch (JMSException e) {
            logger.error("GeneralExample init error", e);
        }


    }

    public void sendMsg() {
        BytesMessage message = null;
        try {
            message = session.createBytesMessage();
            message.writeBytes(new String("bytes message queue").getBytes());
            sender.send(queue, message);
        } catch (JMSException e) {
        }
    }
}
