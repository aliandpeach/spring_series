package com.yk.event;

import java.util.EventListener;

/**
 * 初始化事件控制
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/25 10:13
 */
public interface InitializingListener extends EventListener
{
    void onInitializing(InitializingEvent message);
}
