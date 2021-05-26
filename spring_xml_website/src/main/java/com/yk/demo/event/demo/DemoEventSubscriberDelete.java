package com.yk.demo.event.demo;

import org.springframework.beans.factory.InitializingBean;

/**
 * 订阅者
 */
public class DemoEventSubscriberDelete extends MessageTaskManager implements InitializingBean
{

    @Override
    protected String getTopic()
    {
        return EventType.DELETE.name();
    }

    @Override
    protected void onMessage(MessageForm form)
    {
        System.out.println(form);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
    }
}
