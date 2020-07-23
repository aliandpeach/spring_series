package com.yk.demo.event.demo;

public class MessageForm {

    private MessageTopic messageTopic = MessageTopic.DATA;

    private long id;

    private Object source;

    @Override
    public String toString() {
        return "MessageForm{...}";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MessageTopic getMessageTopic() {
        return messageTopic;
    }

    public void setMessageTopic(MessageTopic messageTopic) {
        this.messageTopic = messageTopic;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
