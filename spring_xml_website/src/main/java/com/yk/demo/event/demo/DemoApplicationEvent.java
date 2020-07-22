package com.yk.demo.event.demo;

import java.util.EventObject;

public class DemoApplicationEvent {


    private MessageUnit messageUnit;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DemoApplicationEvent(MessageUnit source) {
        this.messageUnit = source;
    }

    public MessageUnit getMessageUnit() {
        return messageUnit;
    }
}
