package com.yk.demo.activemq.demo.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueProducer
{
    private Logger logger = LoggerFactory.getLogger("activemq");

    private AtomicInteger integer = new AtomicInteger();

    private QueueConnectionFactory factory = null;

    private QueueConnection connection;

    private QueueSession session;

    private QueueSender sender;

    private Queue queue = null;

    TemporaryQueue tempQueue;
    QueueReceiver receiver;

    public QueueProducer()
    {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try
        {
            connection = factory.createQueueConnection();
            connection.start();
            session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue("Queue.A.queue");
            sender = session.createSender(queue);

            tempQueue = session.createTemporaryQueue();
            receiver = session.createReceiver(tempQueue);
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample init error", e);
        }


    }

    public void sendMsg()
    {
        BytesMessage message = null;
        try
        {
            message = session.createBytesMessage();
            message.writeBytes(new String("bytes message queue" + integer.incrementAndGet()).getBytes());

            message.setJMSReplyTo(tempQueue);
            sender.send(queue, message);
            Message message1 = receiver.receive();
            System.out.println(null != message1 ? ((TextMessage) message1).getText() : "no replay");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
