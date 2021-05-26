package com.yk.demo.event.demo;

public class DemoEventPublisher
{

    public void send(MessageForm messageForm)
    {
        ApplicationContext.getInstance().publishEvent(new ApplicationEvent(messageForm).ofEventType(EventType.ADD.name()));
    }
}
