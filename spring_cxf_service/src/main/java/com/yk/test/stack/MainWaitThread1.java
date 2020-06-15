package com.yk.test.stack;

import java.util.concurrent.TimeUnit;

public class MainWaitThread1
{
    private static Object object = new Object();

    public static void main(String[] args) throws InterruptedException
    {
        Thread th1 = new Thread(() -> {

            try
            {
                synchronized (object)
                {
                    object.wait();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("th1");
        });
        th1.start();
        TimeUnit.MILLISECONDS.sleep(5_000);
        th1.interrupt();
    }
}
