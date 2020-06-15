package com.yk.leetcode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class ZeroEvenOdd {

    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();

    private Map<String, Boolean> finished = new ConcurrentHashMap<>();

    private AtomicInteger next = new AtomicInteger(0);
    private int n;

    public ZeroEvenOdd(int n) {
        this.n = n;
        finished.put("zero", true);
        finished.put("even", false);
        finished.put("odd", false);
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (next.get() <= n) {
            try {
                lock.lock();
                while (!finished.get("zero")) {
//                    System.out.println("zero wait...");
                    condition.await();
//                    System.out.println("zero run...");
                }
                if (next.get() < n)
                    printNumber.accept(0);
                next.incrementAndGet();
                finished.put("zero", false);

                finished.put("even", true);
                finished.put("odd", true);
                condition.signalAll();

            } finally {
                lock.unlock();
            }
        }

        try {
            lock.lock();
            finished.put("even", true);
            finished.put("odd", true);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        while (next.get() <= n) {
            try {
                lock.lock();
                while (!finished.get("even")) {
//                    System.out.println("even wait...");
                    condition.await();
//                    System.out.println("even run...");
                }
                if (next.get() % 2 == 0 && next.get() != 0 && next.get() <= n) {
                    printNumber.accept(next.get());
                    finished.put("even", false);
                    finished.put("zero", true);
                    finished.put("odd", true);
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        while (next.get() <= n) {
            try {
                lock.lock();
                while (!finished.get("odd")) {
//                    System.out.println("odd wait...");
                    condition.await();
//                    System.out.println("odd run...");
                }
                if (next.get() % 2 == 1 && next.get() != 0 && next.get() <= n) {
                    printNumber.accept(next.get());
                    finished.put("odd", false);
                    finished.put("zero", true);
                    finished.put("even", true);
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(100);
        Thread a = new Thread(() -> {
            try {
                zeroEvenOdd.zero(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        a.setName("A");
        Thread b = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                zeroEvenOdd.even(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        b.setName("B");
        Thread c = new Thread(() -> {
            try {
//                TimeUnit.MILLISECONDS.sleep(1000);
                zeroEvenOdd.odd(System.out::print);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        c.setName("C");

        a.start();
        c.start();
        b.start();
    }
}
