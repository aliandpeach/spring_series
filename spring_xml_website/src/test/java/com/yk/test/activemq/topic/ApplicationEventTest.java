package com.yk.test.activemq.topic;

import com.yk.demo.event.demo.DemoEventSubscriberAdd;
import com.yk.demo.event.demo.EventType;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

public class ApplicationEventTest
{
    @Test
    public void testEvent() throws InterruptedException
    {
        com.yk.demo.event.demo.MessageCenter center = new com.yk.demo.event.demo.MessageCenter();
        center.contextInitialized(null);

        com.yk.demo.event.demo.MessageCenter.addSubscribes(new com.yk.demo.event.demo.MessageTaskManager()
        {
            @Override
            protected String getTopic()
            {
                return EventType.ADD.name();
            }

            @Override
            protected void onMessage(com.yk.demo.event.demo.MessageForm form)
            {
                System.out.println(form + " " + this.getClass().getName());
            }
        });

        com.yk.demo.event.demo.MessageCenter.addSubscribes(new DemoEventSubscriberAdd());
        com.yk.demo.event.demo.MessageCenter.addSubscribes(new DemoEventSubscriberAdd());

        com.yk.demo.event.demo.MessageForm form = new com.yk.demo.event.demo.MessageForm();
        form.setSource(new HashMap<>(Collections.singletonMap("info", "success")));
        com.yk.demo.event.demo.MessageCenter.sendMessage(form, EventType.ADD.name());
        Thread.currentThread().join();
    }
}
