package com.yk.demo.activemq.demo.general;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneralExample
{

    private Logger logger = LoggerFactory.getLogger("activemq");

    private AtomicInteger integer = new AtomicInteger();

    private ConnectionFactory factory = null;

    private Connection connection;

    private Session session;

    private Destination destination;

    private MessageProducer producerA;

    private MessageConsumer consumerA;

    public GeneralExample()
    {
        factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        try
        {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("A.general.topic");
            producerA = session.createProducer(destination);
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample init error", e);
        }


        try
        {
            consumerA = session.createConsumer(destination);
            consumerA.setMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    TextMessage textMessage = (TextMessage) message;
                    try
                    {
                        String string = textMessage.getText();
                        Optional.ofNullable(string).ifPresent(System.out::println);
                    }
                    catch (JMSException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample createConsumer error", e);
        }
    }

    public void send()
    {
        try
        {
            TextMessage message = session.createTextMessage("this is a message for topic [A.general.topic]" + integer.incrementAndGet());
            producerA.send(destination, message);
        }
        catch (JMSException e)
        {
            logger.error("GeneralExample send error", e);
        }
    }
}
