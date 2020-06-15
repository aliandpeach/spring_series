package com.yk;

import org.apache.activemq.broker.BrokerService;

public class BrokerServiceMain {
    public static void main(String[] args) {
        BrokerService brokerService = new BrokerService();
        brokerService.setUseJmx(false);
        brokerService.setPersistent(false);
        try {
            brokerService.addConnector("tcp://127.0.0.1:61616");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
