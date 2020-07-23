package com.yk.demo.event.demo;

import java.util.EventObject;

public class DemoApplicationEvent {


    private MessageForm messageForm;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DemoApplicationEvent(MessageForm source) {
        this.messageForm = source;
    }

    public MessageForm getMessageForm() {
        return messageForm;
    }
}
