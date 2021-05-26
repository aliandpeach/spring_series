package com.yk.demo.event.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 订阅者
 */
@Component
public class DemoEventSubscriberAdd extends MessageTaskManager implements InitializingBean
{
    @Override
    protected String getTopic()
    {
        return EventType.ADD.name();
    }

    @Override
    protected void onMessage(MessageForm form)
    {
        System.out.println(form + " aaaaaaa");
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        MessageCenter.addSubscribes(this);
    }
}
