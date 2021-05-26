package com.yk.demo.activemq.service;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageForm<T> implements Serializable
{
    private static final long serialVersionUID = 9043498339425150751L;

    private MessageTopic messageTopic = MessageTopic.ADD;

    private long id;

    private T source;

    public <T> MessageForm<T> ofTopic(MessageTopic messageTopic)
    {
        this.messageTopic = messageTopic;
        return (MessageForm<T>) this;
    }

    @Override
    public String toString()
    {
        return "MessageForm{" +
                "messageTopic=" + messageTopic +
                ", id=" + id +
                ", source=" + source +
                '}';
    }
}
