package com.yk.test.activemq.general;

import com.yk.demo.activemq.demo.general.GeneralExample;
import org.apache.activemq.broker.BrokerService;

import javax.jms.BytesMessage;
import java.util.concurrent.TimeUnit;

public class ActiveMQMainSimple
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


        Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
        {
            while (true)
            {
                System.out.println("1111");
                try
                {
                    TimeUnit.SECONDS.sleep(10);
                }
                catch (InterruptedException interruptedException)
                {
                    interruptedException.printStackTrace();
                }
            }
        });

        new Thread(() ->
        {
            GeneralExample publish = new GeneralExample();
            while (true)
            {
                publish.send();
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        throw new RuntimeException("error le ");
    }
}
