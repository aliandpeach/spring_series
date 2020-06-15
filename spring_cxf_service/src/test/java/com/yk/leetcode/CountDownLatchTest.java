package com.yk.leetcode;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class CountDownLatchTest {

    public static void main(String[] args) {
        CountDownLatch downLatch = new CountDownLatch(10);

        IntStream.range(0, 10).forEach((t) -> {
            new Thread(() -> {
                Thread.currentThread().setName("t-" + t);
                System.out.println("t-" + t + "run");
                downLatch.countDown();
            }).start();
        });
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main run");
    }
}
