package com.yk.threadtest;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ThreadExecutor {
    private boolean finished;

    private Thread executThread;

    private long start;

    public void execute(Runnable task) {
        executThread = new Thread() {
            @Override
            public void run() {
                Thread actural = new Thread(task);
                actural.setDaemon(true);
                actural.start();

                start = System.currentTimeMillis();
                try {
                    actural.join();
                    finished = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("actural thread stop...");
                }
            }
        };

        executThread.start();
    }

    public void shutdown(long mills) {
        start = System.currentTimeMillis();
        while (!finished) {
//            System.out.println("current..." + System.currentTimeMillis());
//            System.out.println("start..." + start);
            if ((System.currentTimeMillis() - start) >= mills) {
                System.out.println("interrupt execute...");
                executThread.interrupt();
                break;
            }
        }
    }

    public static void main(String args[]) {
        ThreadExecutor executor = new ThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("任务执行中");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        executor.shutdown(15000);


        BigInteger bigInteger = BigInteger.probablePrime(2048, new SecureRandom());
        boolean f = bigInteger.isProbablePrime(256);
        System.out.println(f);
    }
}