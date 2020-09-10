package com.yk.demo.activemq.service;

import lombok.Data;

@Data
public class MessageForm {

    private MessageTopic messageTopic = MessageTopic.DATA;

    private long id;

    private Object source;

    @Override
    public String toString() {
        return "MessageForm{" +
                "messageTopic=" + messageTopic +
                ", id=" + id +
                ", source=" + source +
                '}';
    }
}
