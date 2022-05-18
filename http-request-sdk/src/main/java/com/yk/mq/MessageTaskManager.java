package com.yk.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * ActiveMQ路由端消费信息转发至真正的业务订阅者 每一次请求都会产生一个订阅者结束后自动取消订阅
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 09:56
 */
public abstract class MessageTaskManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTaskManager.class);

    /**
     * 二级订阅需要实现的方法
     */
    protected abstract void onMessageTask(Map<String, String> result);

    /**
     * 消费后的默认回复
     *
     * @param message 消费的消息对象
     * @param session session
     */
    protected void replay(Message message, Session session)
    {
        if (null == session)
        {
            return;
        }
        try
        {
            MessageProducer replay = session.createProducer(message.getJMSReplyTo());
            TextMessage replayTest = session.createTextMessage("ok");
            replay.send(replayTest);
        }
        catch (Exception e)
        {
            LOGGER.error("replay message error", e);
            LOGGER.error("replay message error with id={}", "failed");
        }
    }

    /**
     * 业务类需要自定义的主题类型 (SDK中只有一个主题)
     */
    protected String getTopic()
    {
        return MessageCenter.TOPIC;
    }
}
