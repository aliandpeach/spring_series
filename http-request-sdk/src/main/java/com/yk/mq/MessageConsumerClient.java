package com.yk.mq;

import com.yk.exception.SdkException;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * ActiveMQ消费客户端
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 09:56
 */
public class MessageConsumerClient extends Client
{
    @Override
    public void setListener(MessageListener listener)
    {
        try
        {
            receiver.setMessageListener(listener);
        }
        catch (JMSException e)
        {
            throw new SdkException("subscriber error", e);
        }
    }
}
