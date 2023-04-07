package com.yk.bitcoin;

import com.yk.bitcoin.model.Task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyCache
{
    public static final Map<Task, Context> TASK_CONTEXT = new ConcurrentHashMap<>();
}
