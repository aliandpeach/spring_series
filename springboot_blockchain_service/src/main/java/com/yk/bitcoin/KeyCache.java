package com.yk.bitcoin;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KeyCache
{
    public static BlockingQueue<Map<String, String>> keyQueue = new LinkedBlockingQueue<>();

    public static final Object lock = new Object();
}
