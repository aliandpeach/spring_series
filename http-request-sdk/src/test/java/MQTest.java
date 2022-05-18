import cn.hutool.json.JSONUtil;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MQTest
{
    private TopicConnection connection;
    private TopicSubscriber subscriber;
    private Topic topic;

    private ActiveMQSslConnectionFactory factory;

    @Before
    public void connectionFactory() throws Exception
    {
        factory = new ActiveMQSslConnectionFactory();

        factory.setBrokerURL("ssl://localhost.com:61617?tcpNoDelay=true&wireFormat.maxInactivityDuration=0&jms.blobTransferPolicy.defaultUploadUrl=https://localhost.com:8161/fileserver/");

        factory.setUserName("root");
        factory.setPassword("Admin@123");

        factory.setKeyStore("cert/client.ks");
        factory.setKeyStorePassword("Admin@123");

        factory.setTrustStore("cert/client.ts");
        factory.setTrustStorePassword("Admin@123");

        factory.setSendTimeout(120000);
        factory.setCloseTimeout(120000);
    }

    @Test
    public void subscriber() throws JMSException, InterruptedException
    {
        connection = factory.createTopicConnection();
        connection.start();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic("test-response");
        subscriber = session.createSubscriber(topic);

        subscriber.setMessageListener(message ->
        {
            TextMessage textMessage = (TextMessage) message;
            try
            {
                String text = textMessage.getText();
                System.out.println(text);
            }
            catch (JMSException e)
            {
                e.printStackTrace();
            }
        });

        Thread.currentThread().join();
        TimeUnit.SECONDS.sleep(1);
    }
}
