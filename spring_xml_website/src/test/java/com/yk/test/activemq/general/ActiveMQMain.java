package com.yk.test.activemq.general;

import com.yk.demo.activemq.demo.general.GeneralExample;
import org.apache.activemq.broker.BrokerService;

import javax.jms.BytesMessage;

public class ActiveMQMain {

    public static void main(String args[]) throws Exception {
        BrokerService service = new BrokerService();

        service.addConnector("tcp://127.0.0.1:61616");
        service.setUseJmx(true);
        // 设置broker名字
        service.setBrokerName("MyBroker");
        // 是否使用持久化
        service.setPersistent(false);

        service.start();


        GeneralExample publish = new GeneralExample();
        while (true)
        {
            publish.send();
            Thread.sleep(1000);
        }
    }
}
