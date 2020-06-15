package com.volatiletest;

import java.util.concurrent.TimeUnit;

public class VolatileTest3
{
    private static int INT_VALUE = 0;

    public static void main(String[] args) throws InterruptedException
    {
        new Thread(() -> {
            while (INT_VALUE < 50)
            {
                System.out.println("Thread T1 println " + ++INT_VALUE);
                try
                {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (INT_VALUE < 50)
            {
                System.out.println("Thread T2 println " + ++INT_VALUE);
                try
                {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
