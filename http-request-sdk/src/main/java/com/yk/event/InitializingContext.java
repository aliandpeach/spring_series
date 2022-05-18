package com.yk.event;

import com.yk.auth.LoginAuth;
import com.yk.mq.MessageCenter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 初始化事件控制
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/25 10:13
 */
public class InitializingContext
{
    private static final List<InitializingListener> listenerList = new CopyOnWriteArrayList<>();

    static
    {
        listenerList.add(MessageCenter.get());
        listenerList.add(LoginAuth.INSTANCE);
    }

    public void next(InitializingEvent event)
    {
        listenerList.forEach(listener -> listener.onInitializing(event));
    }

    public static InitializingContext getInstance()
    {
        return InitializingContextHolder.INSTANCE;
    }

    private static class InitializingContextHolder
    {
        public static InitializingContext INSTANCE = new InitializingContext();
    }
}
