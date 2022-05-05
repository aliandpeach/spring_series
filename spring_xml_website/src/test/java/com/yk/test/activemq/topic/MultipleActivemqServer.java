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
import java.util.stream.IntStream;

/**
 * 验证同一个主题下的消息从发送到回复这个过程中， 多个producer之间会不会串线 -- 不会 因为返回信息依赖的临时队列是不一样的
 *
 * @author yangk
 * @version 1.0
 * @since 2021/07/16 14:06:52
 */
public class MultipleActivemqServer
{
    public static void main(String[] args)
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        MessageCenter.newInstance(true, -1);
    }
}
