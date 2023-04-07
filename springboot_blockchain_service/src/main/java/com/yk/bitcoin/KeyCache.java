package com.yk.bitcoin;

import com.yk.bitcoin.model.Task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyCache
{
    public static final Map<Task, Context> TASK_CONTEXT = new ConcurrentHashMap<>();

    public static Context runningTaskContext()
    {
        Context context = null;
        for (Map.Entry<Task, Context> entry : KeyCache.TASK_CONTEXT.entrySet())
        {
            context = entry.getValue();
            if (null != context && null != context.getTask() && context.getTask().getState() == 1)
            {
                break;
            }
        }
        return context;
    }
}
