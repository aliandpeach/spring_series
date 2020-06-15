package com.yk.test.stack;

import java.util.concurrent.TimeUnit;

public class MainJoinThread
{
    private static boolean flag = false;

    public static void main(String[] args) throws InterruptedException
    {
        Thread th1 = new Thread(() -> {
            while (true)
            {
                if (flag)
                {
                    Thread inner = new Thread(() -> {

                    });
                    try
                    {
                        inner.start();
                        inner.join();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        th1.start();
        TimeUnit.MILLISECONDS.sleep(10_000);
        th1.interrupt();
        flag = true;
    }
}
