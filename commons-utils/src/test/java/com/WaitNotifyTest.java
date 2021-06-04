package com;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

public class WaitNotifyTest
{
    @Test
    public void test() throws InterruptedException
    {
        List<String> locks = new ArrayList<>();
        IntStream.range(0, 5).forEach(i ->
        {
            String lock = UUID.randomUUID().toString().replace("-", "");
            locks.add(lock);
        });
        locks.forEach(l ->
        {
            new Thread(() ->
            {
                long start = System.currentTimeMillis();
                final String lock = l.intern();
                synchronized (lock)
                {

                    try
                    {
                        lock.wait(8 * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                long end = System.currentTimeMillis();
                if (end - start >= 8 * 1000)
                {
                    System.out.println(l + " timeout " + (end - start));
                }
                else
                {
                    System.out.println(l + " notify!!! " + (end - start));
                }
            }).start();
        });
        TimeUnit.SECONDS.sleep(2);
        new Thread(() ->
        {
            String lock = locks.get(new Random().nextInt(5)).intern();
            System.out.println("notify " + lock);
            synchronized (lock)
            {
                lock.notifyAll();
            }
        }).start();
        new Thread(() ->
        {
            String lock = locks.get(new Random().nextInt(5)).intern();
            synchronized (lock)
            {
                lock.notifyAll();
            }
            System.out.println("notify " + lock);
        }).start();
        Thread.currentThread().join();
    }

    @Test
    public void testParkNanos() throws InterruptedException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(600);
        for (int i = 0; i < 600; i++)
        {
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        LockSupport.parkNanos(1);
                    }
                }
            });
        }
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
