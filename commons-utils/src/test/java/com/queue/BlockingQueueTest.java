package com.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/07/25 19:00:46
 */
public class BlockingQueueTest
{
    public static void main(String[] args)
    {
        BoundedBlockingQueue<List<String>> boundedBlockingQueue = new BoundedBlockingQueue<List<String>>(new LinkedBlockingQueue<>(), 5);
        ExecutorService read = Executors.newFixedThreadPool(3);
        ExecutorService writer = Executors.newFixedThreadPool(5);

        new Thread(() ->
        {
            while (true)
            {
                sleep(1000);
//                System.out.println("size = " + boundedBlockingQueue.size());
                System.out.println("size = " + boundedBlockingQueue.size());
            }
        }).start();

        new Thread(() ->
        {
            for (int k = 0; k < 5; k++)
            {
                final int iii = k;
                writer.execute(() ->
                {
                    try
                    {
                        List<String> list = boundedBlockingQueue.take();
                        System.out.println("k = " + iii + ", " + list.size());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
            sleep(300);
        }).start();


        sleep(3000);
        new Thread(() ->
        {
            for (int k = 0; k < 1; k++)
            {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 100; i++)
                    list.add("string-" + i);
                read.execute(() ->
                {
                    try
                    {
                        boundedBlockingQueue.put(list);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                });
                sleep(100);
            }
        }).start();
    }

    private static void sleep(int mills)
    {
        try
        {
            Thread.sleep(mills);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
