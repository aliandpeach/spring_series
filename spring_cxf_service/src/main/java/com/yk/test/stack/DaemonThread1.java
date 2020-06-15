package com.yk.test.stack;

import java.util.concurrent.TimeUnit;

public class DaemonThread1
{
    public static void main(String[] args)
    {
        Thread t = new Thread(() -> {
            Thread inner = new Thread(() -> {
                while (true)
                {
                    System.out.println("inner thread running...");
                    try
                    {
                        TimeUnit.MILLISECONDS.sleep(300);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            inner.setDaemon(true);
            inner.start();
            try
            {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("t thread finished!");
        });
        t.start();

        Thread t2 = new Thread(() -> {
            while (true)
            {
                System.out.println("t2 thread is running!");
                try
                {
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
    }
}
