package com.yk.demo.event.demo;

public class EventConsumer implements DemoApplicationListener {

    @Override
    public void onApplicationEvent(DemoApplicationEvent e) {
        System.out.println("?");
    }
}
