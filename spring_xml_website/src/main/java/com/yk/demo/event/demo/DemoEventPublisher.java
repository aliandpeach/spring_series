package com.yk.demo.event.demo;

public class DemoEventPublisher {

    public void send(MessageForm messageForm) {
        DemoApplicationContext.getInstance().publishEvent(new DemoApplicationEvent(messageForm));
    }
}
