package com.yk.leetcode;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    private final static Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {


        new Thread(() -> {
            semaphore.release();
            System.out.println("Thread B");
        }).start();

        Thread.sleep(3000);

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("Thread A");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
