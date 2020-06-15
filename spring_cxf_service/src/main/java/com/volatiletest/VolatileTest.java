package com.volatiletest;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VolatileTest
{
    private static volatile boolean flag = false;

    public static void main(String[] args) throws InterruptedException
    {
        new Thread(() -> {
            while (!flag)
            {
//                Optional.of("flag false running...").ifPresent(System.out::println);
            }
            Optional.of("flag true stoped...").ifPresent(System.out::println);
        }).start();

        TimeUnit.MILLISECONDS.sleep(1);
        new Thread(() -> {
            flag = true;
        }).start();
    }
}
