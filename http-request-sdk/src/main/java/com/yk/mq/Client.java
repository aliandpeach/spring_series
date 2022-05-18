package com.yk.mq;

import com.yk.core.CommonInfo;
import com.yk.exception.SdkException;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import java.util.Objects;
import java.util.UUID;

/**
 * ActiveMQ抽象消费端- 使用队列模式, 该模式下, 多个客户端轮换消费消息, 单个消息不会被多个客户端重复消费
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 09:56
 */
public abstract class Client
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private String id = UUID.randomUUID().toString().replace("-", "");

    private ActiveMQSslConnectionFactory factory;

    private TopicConnection connection;

    protected TopicSession session;

    private Topic topic;

    protected TopicSubscriber receiver;

    private CommonInfo info;

    private boolean conn;

    private void connectionFactory() throws Exception
    {
        factory = new ActiveMQSslConnectionFactory();

        factory.setBrokerURL(info.getBrokerUrl());

        factory.setUserName(info.getUsername());
        factory.setPassword(info.getPassword());

        factory.setKeyStore(info.getKeystore());
        factory.setKeyStorePassword(info.getKeystorePasswd());

        factory.setTrustStore(info.getTruststore());
        factory.setTrustStorePassword(info.getTruststorePasswd());

        factory.setSendTimeout(info.getSendTimeout());
        factory.setCloseTimeout(info.getCloseTimeout());
    }

    public synchronized void connect(CommonInfo commonInfo, String topicName, boolean forceReConnect)
    {
        if (conn && !forceReConnect)
        {
            return;
        }
        this.info = commonInfo;
        try
        {
            connectionFactory();
            connection = factory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            receiver = session.createSubscriber(topic);
        }
        catch (Exception e)
        {
            LOGGER.error("activemq connect error url", e);
            throw new SdkException("activemq connect error ", e);
        }
        conn = true;
    }

    public abstract void setListener(MessageListener listener);

    public TopicSession getSession()
    {
        return session;
    }

    @Override
    public String toString()
    {
        return "Client{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
