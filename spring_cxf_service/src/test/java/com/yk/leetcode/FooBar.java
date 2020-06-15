package com.yk.leetcode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class FooBar {
    private int n;

    public FooBar(int n) {
        this.n = n;
        finished.put("foo", false);
        finished.put("bar", true);
    }

    private final Object lock = new Object();

    private Map<String, Boolean> finished = new ConcurrentHashMap<>();

    public void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {

            // printFoo.run() outputs "foo". Do not change or remove this line.
            synchronized (lock) {
                while (!finished.get("bar")) {
                    lock.wait();
                }
            }
            printFoo.run();
            synchronized (lock) {
                finished.put("foo", true);
                finished.put("bar", false);
                lock.notifyAll();
            }
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {

        for (int i = 0; i < n; i++) {

            // printBar.run() outputs "bar". Do not change or remove this line.
            synchronized (lock) {
                while (!finished.get("foo")) {
                    lock.wait();
                }
            }
            printBar.run();
            synchronized (lock) {
                finished.put("bar", true);
                finished.put("foo", false);
                lock.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        FooBar fooBar = new FooBar(10);
        Thread a = new Thread(() -> {
            try {
                fooBar.foo(new Main.MyRunnable("foo"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        a.setName("A");
        Thread b = new Thread(() -> {
            try {
                fooBar.bar(new Main.MyRunnable("bar"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        b.setName("B");
        a.start();
        b.start();
    }
}