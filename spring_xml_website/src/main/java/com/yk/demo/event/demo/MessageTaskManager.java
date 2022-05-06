package com.yk.demo.event.demo;

import com.google.common.base.Objects;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 抽象出来的任务类，订阅者通过实现该抽象类方法，实现订阅
 */
public abstract class MessageTaskManager
{
    private final String id = UUID.randomUUID().toString();

    protected abstract String getTopic();

    protected abstract void onMessage(MessageForm form);

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTaskManager that = (MessageTaskManager) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
