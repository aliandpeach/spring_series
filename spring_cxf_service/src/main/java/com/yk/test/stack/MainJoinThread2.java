package com.yk.test.stack;

import java.util.concurrent.TimeUnit;

public class MainJoinThread2
{

    static boolean fl = true;
    public static void main(String[] args) throws InterruptedException
    {
        Thread th1 = new Thread(() -> {
            Thread inner = new Thread(() -> {
                while (fl) {
                    //子线程一直执行
                    System.out.println("inner running...");
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            inner.start();

            try {
                inner.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                inner.interrupt();
                Thread t = new Thread();
            }
            System.out.println("th1");
        });
        th1.start();
        TimeUnit.MILLISECONDS.sleep(5_000);
        th1.interrupt();
    }
}
