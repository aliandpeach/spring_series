package com.yk.test.stack;

import java.util.Optional;

public class MainJoin
{
    public static void main(String[] args)
    {
        Thread th1 = new Thread(() -> {
            Optional.of(Thread.currentThread()).ifPresent(System.out::println);
            try
            {
                Thread.currentThread().join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
        th1.start();
        Optional.of(Thread.currentThread() + " is done!").ifPresent(System.out::println);
    }
}
