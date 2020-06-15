package com.yk.leetcode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class ZeroEvenOdd2 {

    Semaphore zero = new Semaphore(1);
    Semaphore even = new Semaphore(0);
    Semaphore odd = new Semaphore(0);

    private int n;
    AtomicInteger next = new AtomicInteger(0);

    public ZeroEvenOdd2(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (next.get() <= n) {
//            System.out.println("zero acquire");
            zero.acquire();
//            System.out.println("zero run");
            if (next.get() < n) {
                printNumber.accept(0);
            }
            next.incrementAndGet();

            if (next.get() % 2 == 0) {
                even.release();
            } else {
                odd.release();
            }
        }
        even.release();
        odd.release();
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
//        System.out.println("even enter next=" + next.get());
        while (next.get() <= n) {
//            System.out.println("even acquire");
            even.acquire();
            if (next.get() > n) {
                return;
            }
//            System.out.println("even run");
            printNumber.accept(next.get());
            zero.release();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        while (next.get() <= n) {
//            System.out.println("odd acquire");
            odd.acquire();
            if (next.get() > n) {
                return;
            }
//            System.out.println("odd run");
            printNumber.accept(next.get());
            zero.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ZeroEvenOdd2 zeroEvenOdd = new ZeroEvenOdd2(2);
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
