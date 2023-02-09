package com.yk.bitcoin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableTest3 implements Runnable
{
    private final AtomicInteger count;

    public RunnableTest3(AtomicInteger count)
    {
        this.count = count;
    }

    @Override
    public void run()
    {
        System.out.println(Thread.currentThread());
        while (true)
        {
            synchronized (this)
            {
                if (count.get() > 10000)
                {
                    System.out.println(count);
                    count.set(1);
                    break;
                }
                count.addAndGet(1);
            }
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        AtomicInteger integer = new AtomicInteger(0);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });
        AtomicInteger count = new AtomicInteger(1);
        RunnableTest3 runnableTest = new RunnableTest3(count);
        for (int i = 0; i < 5; i++)
        {
            executor.scheduleWithFixedDelay(runnableTest, 0, 1, TimeUnit.MILLISECONDS);
        }
        try
        {
            Thread.sleep(20000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
