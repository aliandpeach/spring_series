package com.yk.demo.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Test {
    public static void main(String[] args) {

        final List<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }

        Thread th2 = new Thread(() -> {
            list.remove(4);
            System.out.println("list.remove(4)");
        });
        Thread th1 = new Thread(() -> {
            /*try {
                th2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            for (int item : list) {
                System.out.println("遍历元素：" + item);
            }
        });
        th1.start();
        th2.start();
        try {
            Class.forName("com.yk.demo.event.demo.DemoEventConsumerProxy").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
