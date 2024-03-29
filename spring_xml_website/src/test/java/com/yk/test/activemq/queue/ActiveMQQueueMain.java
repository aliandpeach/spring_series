package com.yk.test.activemq.queue;

import com.yk.demo.activemq.demo.queue.QueueConsumer;
import com.yk.demo.activemq.demo.queue.QueueConsumer2;
import com.yk.demo.activemq.demo.queue.QueueProducer;
import com.yk.demo.activemq.demo.topic.TopicConsumer;
import com.yk.demo.activemq.demo.topic.TopicProducer;
import org.apache.activemq.broker.BrokerService;

public class ActiveMQQueueMain
{

    public static void main(String args[]) throws Exception
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        BrokerService service = new BrokerService();

        service.addConnector("tcp://127.0.0.1:61616");
        service.setUseJmx(true);
        // 设置broker名字
        service.setBrokerName("MyBroker");
        // 是否使用持久化
        service.setPersistent(false);

        service.start();


        new Thread(() ->
        {
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            new QueueConsumer();
            new QueueConsumer2();
        }).start();
        QueueProducer producer = new QueueProducer();
        while (true)
        {
            producer.sendMsg();
            Thread.sleep(1000);
        }
    }
}
