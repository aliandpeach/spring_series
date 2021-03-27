package com.yk.bitcoin;

import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleTest
{
    @Test
    public void testFixedRate() throws InterruptedException
    {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100, new ThreadFactory()
        {
            AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "testFixedRate-thread-" + integer.getAndIncrement());
            }
        });
        executor.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(Thread.currentThread().getName() + " , enter Time= " + System.currentTimeMillis());
                try
                {
                    Thread.sleep(25000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " , outxx Time= " + System.currentTimeMillis());
            }
        }, 1, 3, TimeUnit.SECONDS);

        Thread.currentThread().join();
    }

    @Test
    public void testFixedDelay() throws InterruptedException
    {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100, new ThreadFactory()
        {
            AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "testFixedRate-thread-" + integer.getAndIncrement());
            }
        });
        executor.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(Thread.currentThread().getName() + " , enter Time= " + System.currentTimeMillis());
                try
                {
                    Thread.sleep(15000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " , outxx Time= " + System.currentTimeMillis());
            }
        }, 1, 3, TimeUnit.SECONDS);

        Thread.currentThread().join();
    }
    @Test
    public void testSchedule() throws InterruptedException
    {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100, new ThreadFactory()
        {
            AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "testFixedRate-thread-" + integer.getAndIncrement());
            }
        });
        executor.schedule(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(Thread.currentThread().getName() + " , enter Time= " + System.currentTimeMillis());
                try
                {
                    Thread.sleep(2);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " , outxx Time= " + System.currentTimeMillis());
            }
        }, 5, TimeUnit.SECONDS);

        Thread.currentThread().join();
    }
}
