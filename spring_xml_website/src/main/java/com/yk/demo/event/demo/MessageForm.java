package com.yk.demo.event.demo;

import lombok.Data;

@Data
public class MessageForm
{

    private long id;

    private Object source;

    @Override
    public String toString()
    {
        return "MessageForm{" +
                "id=" + id +
                ", source=" + source +
                '}';
    }
}
