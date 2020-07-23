package com.yk.demo.event.demo;

import java.util.EventListener;

/**
 * 这里可以重构为ServiceLoader的方式加载接口实现类
 */
public interface DemoApplicationListener<T> {

    void onApplicationEvent(DemoApplicationEvent e);
}
