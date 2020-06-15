package com.yk.leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Foo {

    public Foo() {
        finished.put("first", false);
        finished.put("second", false);
    }

    private final Object lock = new Object();

    private Map<String, Boolean> finished = new ConcurrentHashMap<>();


    public void first(Runnable printFirst) throws InterruptedException {

        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
        synchronized (lock) {
            finished.put("first", true);
            lock.notifyAll();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {

        // printSecond.run() outputs "second". Do not change or remove this line.
        synchronized (lock) {
            while (!finished.get("first")) {
                lock.wait();
            }
        }
        printSecond.run();
        synchronized (lock) {
            finished.put("second", true);
            lock.notifyAll();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {

        // printThird.run() outputs "third". Do not change or remove this line.
        synchronized (lock) {
            while (!finished.get("second")) {
                lock.wait();
            }
        }
        printThird.run();
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
