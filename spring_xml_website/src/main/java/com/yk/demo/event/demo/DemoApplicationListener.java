package com.yk.demo.event.demo;

import java.util.EventListener;

public interface DemoApplicationListener {

    void onApplicationEvent(DemoApplicationEvent e);
}
