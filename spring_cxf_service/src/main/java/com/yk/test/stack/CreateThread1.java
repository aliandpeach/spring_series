package com.yk.test.stack;

import java.util.stream.IntStream;

public class CreateThread1
{

    private static int COUNTER = 0;

    public static void main(String args[])
    {
        System.out.println(1 << 23);
        System.out.println(1 << 24);
        System.out.println(1 << 25);
        System.out.println(1 << 26);
        System.out.println(1 << 30);
        Thread th = new Thread(null, () -> {
            try
            {
                    add(0);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                System.out.println(COUNTER);
            }

        }, "", 1 << 30);
        th.start();
    }

    public static void add(int i)
    {
        ++COUNTER;
        add(i + 1);
    }
}
