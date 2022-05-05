package com.yk.activemq.service;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageForm implements Serializable
{
    private static final long serialVersionUID = 9043498339425150751L;

    private MessageTopic messageTopic = MessageTopic.ADD;

    private long id;

    private Object source;

    public MessageForm ofTopic(MessageTopic messageTopic)
    {
        this.messageTopic = messageTopic;
        return this;
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
