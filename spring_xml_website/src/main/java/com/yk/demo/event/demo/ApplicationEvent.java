package com.yk.demo.event.demo;

import java.util.EventObject;

public class ApplicationEvent extends EventObject
{
    private String eventType;

    private MessageForm messageForm;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationEvent(MessageForm source)
    {
        super(source);
        this.messageForm = source;
    }

    public MessageForm getMessageForm()
    {
        return messageForm;
    }

    public ApplicationEvent ofEventType(String eventType)
    {
        this.eventType = eventType;
        return this;
    }

    public String getEventType()
    {
        return eventType;
    }
}
