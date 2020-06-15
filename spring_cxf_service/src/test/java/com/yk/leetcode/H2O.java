package com.yk.leetcode;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class H2O {

    CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    Semaphore hydrogen = new Semaphore(2);
    Semaphore oxygen = new Semaphore(1);


    public H2O() {

    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {

        // releaseHydrogen.run() outputs "H". Do not change or remove this line.
        hydrogen.acquire();
        releaseHydrogen.run();
        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        hydrogen.release();
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {

        // releaseOxygen.run() outputs "O". Do not change or remove this line.
        oxygen.acquire();
        releaseOxygen.run();
        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        oxygen.release();
    }

    public static void main(String[] args) {
        H2O h2O = new H2O();
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
