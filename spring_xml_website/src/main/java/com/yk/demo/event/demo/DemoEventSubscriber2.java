package com.yk.demo.event.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 订阅者
 */
public class DemoEventSubscriber2 extends MessageTaskManager implements InitializingBean {

    @Override
    protected MessageTopic getMessageTopic() {
        return MessageTopic.DATA;
    }

    @Override
    protected void onMessage(MessageForm form) {
        System.out.println(form);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
