package com.yk.test.activemq.topic;

import com.yk.demo.activemq.service.MessageCenter;
import com.yk.demo.activemq.service.MessageForm;
import com.yk.demo.activemq.service.MessageTaskManager;
import com.yk.demo.activemq.service.MessageTopic;
import org.junit.Assert;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 验证同一个主题下的消息从发送到回复这个过程中， 多个producer之间会不会串线 -- 不会 因为返回信息依赖的临时队列是不一样的
 *
 * @author yangk
 * @version 1.0
 * @since 2021/07/16 14:06:52
 */
public class MultipleProducer
{
    public static void main(String[] args) throws InterruptedException
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        MessageCenter<Map<String, String>> center = MessageCenter.<Map<String, String>>newInstance(true, 0);
        center.addSubscribes(new MessageTaskManager<Map<String, String>>()
        {
            @Override
            public void onMessageTask(MessageForm<Map<String, String>> messageForm)
            {
                Assert.assertTrue(true);
            }

            @Override
            public void replay(Session session, Message message, MessageForm<Map<String, String>> form) throws JMSException
            {
                if (null == session)
                {
                    return;
                }
                String replayInfo = "replay..." + MessageTopic.ADD.name();
                if (null != form && null != form.getSource().get("taskId"))
                {
                    replayInfo = form.getSource().get("taskId");
                }
                if (replayInfo.startsWith("5_"))
                {
                    return;
                }
                TextMessage replayText = session.createTextMessage(replayInfo);
                MessageProducer replay = session.createProducer(message.getJMSReplyTo());
                replay.send(replayText);
            }

            @Override
            public MessageTopic getTopic()
            {
                return MessageTopic.ADD;
            }
        });

        /**
         * 启动30个producer 给 同一个主题发送消息
         */
        ExecutorService service = Executors.newFixedThreadPool(100);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        IntStream.range(0, 30).forEach(_index ->
        {
            futureList.add(CompletableFuture.runAsync(() ->
            {
                MessageForm<Map<String, String>> form = new MessageForm<>().ofTopic(MessageTopic.ADD);
                String info = _index + "_" + UUID.randomUUID().toString();
                form.setSource(new HashMap<>(Collections.singletonMap("taskId", info)));
                String replay = center.sendMessage(form);
                System.out.println(Thread.currentThread().getName() + " send   info = " + info + "\n" + Thread.currentThread().getName() + " replay info = " + replay + "\n");
            }, service));
        });

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
    }
}
