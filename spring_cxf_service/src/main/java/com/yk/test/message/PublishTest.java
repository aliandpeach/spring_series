package com.yk.test.message;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;

public class PublishTest
{

    private TopicConnection topicConnection;

    private TopicConnectionFactory topicConnectionFactory;

    private TopicPublisher topicPublisher;

    private TopicSession topicSession;

    private TopicSubscriber topicSubscriber;

    private TopicSubscriber topicSubscriber2;

    private Topic topic;

    public void publish()
    {
        try
        {
            topicConnectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicConnection.start();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = topicSession.createTopic("a");
//            Topic topic2 = topicSession.createTopic("b");
            topicPublisher = topicSession.createPublisher(topic);


            topicSubscriber = topicSession.createSubscriber(topic);
            topicSubscriber2 = topicSession.createSubscriber(topic);
            topicSubscriber.setMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    System.out.println(message);
                    BytesMessage bytesMessage = (BytesMessage) message;
                    try
                    {
                        int len = (int) bytesMessage.getBodyLength();
                        byte[] buffer = new byte[len];
                        bytesMessage.readBytes(buffer);
                        System.out.println(new String(buffer, 0, len));
                    }
                    catch (JMSException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            topicSubscriber.setMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    System.out.println(message);
                    BytesMessage bytesMessage = (BytesMessage) message;
                    try
                    {
                        int len = (int) bytesMessage.getBodyLength();
                        byte[] buffer = new byte[len];
                        bytesMessage.readBytes(buffer);
                        System.out.println(new String(buffer, 0, len));
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
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws Exception
    {
        BrokerService brokerService = new BrokerService();
        brokerService.addConnector("tcp://127.0.0.1:61616");
        brokerService.setUseJmx(true);
        // 设置broker名字
        brokerService.setBrokerName("MyBroker");
        // 是否使用持久化
        brokerService.setPersistent(false);

        brokerService.start();


        PublishTest publish = new PublishTest();
        publish.publish();
        BytesMessage bytesMessage = publish.topicSession.createBytesMessage();
        bytesMessage.writeBytes("aaaaaaaabb".getBytes());

        while (true)
        {
            publish.topicPublisher.publish(publish.topic, bytesMessage);
            Thread.sleep(1000);
        }
    }
}
