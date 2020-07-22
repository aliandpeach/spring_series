package com.yk.demo.event.demo;

public class MainTest {

    public static void main(String[] args) {

        DemoApplicationContext demo = new DemoApplicationContext();

        demo.addApplicationListener(event -> {
            MessageUnit source = event.getMessageUnit();
            System.out.println("检测到事件源为字符串类型：事件源变为" + source);
        });

        /**
         * 发布事件
         */
        demo.publishEvent(new DemoApplicationEvent(new MessageUnit()));

    }
}
