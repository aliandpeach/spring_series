package com.yk.core;

import com.yk.mq.MessageTaskManager;

/**
 * 请求参数抽象
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 10:13
 */
public abstract class Request
{
    protected String type;

    protected FileInfo fileInfo;

    protected boolean async;

    protected MessageTaskManager taskManager;

    private ExecutorListener listener;

    public void ofListener(ExecutorListener listener)
    {
        this.listener = listener;
    }

    public ExecutorListener getListener()
    {
        return listener;
    }

    /**
     * 构造函数
     *
     * @param type 类型
     */
    public Request(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }


    public FileInfo getFileInfo()
    {
        return fileInfo;
    }

    public boolean isAsync()
    {
        return async;
    }

    public MessageTaskManager getTaskManager()
    {
        return taskManager;
    }
}
