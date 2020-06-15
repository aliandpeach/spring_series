package com.yk.leetcode;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class H2O2 {


    Object lock = new Object();

    AtomicInteger hydrogen = new AtomicInteger(0);
    AtomicInteger oxygen = new AtomicInteger(0);

//    Semaphore hydrogen = new Semaphore(2);
//    Semaphore oxygen = new Semaphore(1);


    public H2O2() {

    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {

        // releaseHydrogen.run() outputs "H". Do not change or remove this line.
//        hydrogen.acquire();
        try {
            synchronized (lock) {
                while (hydrogen.get() == 2) {
                    lock.wait();
                }
                releaseHydrogen.run();
                hydrogen.incrementAndGet();
                hydrogen = new AtomicInteger(0);
                lock.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        hydrogen.release();
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {

        // releaseOxygen.run() outputs "O". Do not change or remove this line.
//        oxygen.acquire();
        try {
            synchronized (lock) {
                while (oxygen.get() == 1) {
                    lock.wait();
                }
                releaseOxygen.run();
                oxygen.incrementAndGet();
                oxygen = new AtomicInteger(0);
                lock.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        oxygen.release();
    }

    public static void main(String[] args) {
        H2O2 h2O = new H2O2();
        String input = "OOHHHH";
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        System.out.print("H");
                    }
                };
                try {
                    h2O.hydrogen(run);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        System.out.print("O");
                    }
                };
                try {
                    h2O.oxygen(run);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
