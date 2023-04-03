package com.queue;

import com.yk.queue.BoundedBlockingQueue;

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
    /**
     * 只有通过 execute 提交的任务，才能将它抛出的异常交给未捕获异常处理器，而通过 submit 提交的任务，无论是抛出的未检査异常还是已检査异常，都将被认为是任务返回状态的一部分。
     * 如果一个由 submit 提交的任务由于抛出了异常而结束，那么这个异常将被 Future.get 封装在 ExecutionException 中重新抛出。
     */
    public static void main(String[] args)
    {
        BoundedBlockingQueue<String> boundedBlockingQueue = new BoundedBlockingQueue<>(new LinkedBlockingQueue<>(), 5);
        AtomicInteger producerIndex = new AtomicInteger(0);
        new Thread(() ->
        {
            while (true)
            {
                try
                {
                    boundedBlockingQueue.put(producerIndex.incrementAndGet() + "");
                    System.out.println("put queue value" + producerIndex.get());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        ExecutorService s2 = Executors.newFixedThreadPool(5);
        new Thread(() ->
        {
            while (true)
                s2.execute(() ->
                {
                    try
                    {
                        String value = boundedBlockingQueue.take();
                        System.out.println("take queue value" + value);
                        while (true)
                        {

                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                });
        }).start();
    }

    public static void main2(String[] args)
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
