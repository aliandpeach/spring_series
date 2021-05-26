package com.yk.demo.activemq.service;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * 业务处理类
 */
public abstract class MessageTaskManager<T>
{

    private String id = UUID.randomUUID().toString();

    /**
     * 业务类需要实现的订阅方法
     *
     * @param messageForm messageForm
     */
    protected abstract void onMessageTask(MessageForm<T> messageForm);

    /**
     * 业务类需要自定义的主题类型
     */
    public abstract MessageTopic getTopic();

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTaskManager<?> that = (MessageTaskManager<?>) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
