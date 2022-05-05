package com.yk.demo.activemq.demo.queue;

import lombok.SneakyThrows;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * 使用队列 多个消费端分别消费，不会有一条信息被重复消费的情形
 */
public class QueueConsumer2
{
    private Logger logger = LoggerFactory.getLogger("activemq");

    private QueueConnectionFactory factory = null;

    private QueueConnection connection;

    private QueueSession session;

    private QueueReceiver receiver;

    MessageProducer replay;

    public QueueConsumer2()
    {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try
        {
            connection = factory.createQueueConnection();
            connection.start();
            session = connection.createQueueSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("Queue.A.queue");
            receiver = session.createReceiver(queue);

            receiver.setMessageListener(new MessageListener()
            {
                @SneakyThrows
                @Override
                public void onMessage(Message message)
                {
                    BytesMessage bytesMessage = (BytesMessage) message;
                    byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(buffer);
                    System.out.println(new String(buffer, 0, (int) bytesMessage.getBodyLength()));

                    try
                    {
                        replay = session.createProducer(message.getJMSReplyTo());
                        TextMessage textMessage = session.createTextMessage("replay2........");
                        replay.send(textMessage);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample init error", e);
        }
    }
}
