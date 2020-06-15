package com.volatiletest;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VolatileTest2
{
    private static int INT_VALUE = 0;

    public static void main(String[] args) throws InterruptedException
    {
        new Thread(() -> {
            while (INT_VALUE < 5)
            {
                if (INT_VALUE > 0)
                {
                    System.out.println("Thread read println " + INT_VALUE);
                }
            }
        }).start();

        TimeUnit.MILLISECONDS.sleep(1);
        new Thread(() -> {
            while (INT_VALUE < 5)
            {
                System.out.println("Thread write println " + ++INT_VALUE);
                try
                {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
