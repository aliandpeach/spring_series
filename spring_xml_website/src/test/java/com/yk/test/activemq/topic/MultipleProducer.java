package com.yk.test.activemq.topic;

import com.yk.activemq.service.MessageCenter;
import com.yk.activemq.service.MessageForm;
import com.yk.activemq.service.MessageTopic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
        MessageCenter center = MessageCenter.newInstance(false, 1);
        /**
         * 启动30个producer 给 同一个主题发送消息
         */
        ScheduledExecutorService service = Executors.newScheduledThreadPool(100);
        AtomicInteger index = new AtomicInteger(0);
        service.scheduleWithFixedDelay(() ->
        {
            MessageForm form = new MessageForm().ofTopic(MessageTopic.ADD);
            int i = index.incrementAndGet();
            User user = new User("name" + i, "role" + i);
            form.setSource(user);
            String replay = center.sendMessage(form);
            System.out.println(Thread.currentThread().getName() + " send info = " + user + "\n" + Thread.currentThread().getName() + " replay info = " + replay + "\n");
        }, 0, 2, TimeUnit.SECONDS);

//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
    }
}
