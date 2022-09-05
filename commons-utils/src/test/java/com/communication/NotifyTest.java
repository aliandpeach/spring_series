package com.communication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 多生产者-多消费者，使用notify会产生deadlock的现象（实际上是所有的生产者和消费者都进入到了wait状态）
 * 产生的原因是: notify每次只能从等待的所有线程中唤醒一个线程, 结果它没有唤醒对方的线程 (生产者没有唤醒消费者，反而唤醒了生产者, 或者消费者没有唤醒生产者，反而唤醒了消费者)
 *
 * @author yangk
 * @version 1.0
 * @since 2022/07/26 15:45:06
 */
public class NotifyTest
{
    public static void main(String[] args)
    {
        final Object lock = new Object();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 10; i++)
        {
            new Thread(() ->
            {
                while (true)
                {

                    synchronized (lock)
                    {
                        while (queue.size() == 0)
                        {
                            try
                            {
                                System.out.println("consumer : " + Thread.currentThread().getName() + " waited... , queue size=" + queue.size());
                                lock.wait();
                                System.out.println("consumer : " + Thread.currentThread().getName() + " notified..., queue size=" + queue.size());
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        queue.poll();

                        lock.notify();// 唤醒一个等待中的线程(这里希望的是唤醒生产者)
                    }
                    sleep(100);
                }
            }).start();
        }

        sleep(1000);


        for (int i = 0; i < 10; i++)
        {
            final int index = i;
            new Thread(() ->
            {
                while (true)
                {
                    synchronized (lock)
                    {
                        while (queue.size() >= 1)
                        {
                            try
                            {
                                System.out.println("producer" + index + " : " + Thread.currentThread().getName() + " waited..., queue size=" + queue.size());
                                lock.wait();
                                System.out.println("producer" + index + " : " + Thread.currentThread().getName() + " notified..., queue size=" + queue.size());
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        queue.offer(System.currentTimeMillis() + "");

                        lock.notify();// 唤醒一个等待中的线程(这里希望的是唤醒消费者)
                    }
                    sleep(1);
                }
            }).start();
        }
    }

    private static void sleep(long mills)
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
