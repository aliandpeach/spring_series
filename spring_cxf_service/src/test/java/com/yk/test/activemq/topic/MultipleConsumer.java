package com.yk.test.activemq.topic;

import com.yk.activemq.service.MessageCenter;
import com.yk.activemq.service.MessageForm;
import com.yk.activemq.service.MessageTaskManager;
import com.yk.activemq.service.MessageTopic;
import org.junit.Assert;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/05/05 15:31:29
 */
public class MultipleConsumer
{
    public static void main(String args[])
    {
        MessageCenter center = MessageCenter.newInstance(false, 2);
        center.addSubscribes(new MessageTaskManager()
        {
            @Override
            public void onMessageTask(MessageForm messageForm)
            {
                Assert.assertTrue(true);
                System.out.println(messageForm);
            }

            @Override
            public void replay(Session session, Message message)
            {
                if (null == session)
                {
                    return;
                }
                try
                {
                    String replayInfo = "replay..." + MessageTopic.ADD.name();
                    TextMessage replayText = session.createTextMessage(replayInfo);
                    MessageProducer replay = session.createProducer(message.getJMSReplyTo());
                    replay.send(replayText);
                }
                catch (JMSException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.ADD;
            }
        });
    }
}
