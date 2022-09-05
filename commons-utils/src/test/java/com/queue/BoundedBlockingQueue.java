package com.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有界阻塞队列
 *
 * <p>有界阻塞队列类，用于线程池内部的任务队列，可以设置任务队列的最大长度，防止因大量读取数据出现JVM内存溢出错误。</p>
 *
 * @param <T>
 * @author guorui@adfsoft.com
 */
public class BoundedBlockingQueue<T>
{

    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private int maxQueueSize;

    private int count;

    private String name;

    private BlockingQueue<T> queue;

    public BoundedBlockingQueue(BlockingQueue<T> queue, int maxQueueSize)
    {
        this("DefaultBlockingQueue", queue, maxQueueSize);
    }

    public BoundedBlockingQueue(String name, BlockingQueue<T> queue, int maxQueueSize)
    {
        this.name = name;
        this.queue = queue;
        this.maxQueueSize = maxQueueSize;
    }

    public void put(T object) throws InterruptedException
    {
        lock.lock();
        try
        {
            while (count == maxQueueSize)
            {
                notFull.await();
            }
            queue.put(object);
            ++count;
            notEmpty.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException
    {
        lock.lock();
        try
        {
            while (count == 0)
            {
                System.out.println(Thread.currentThread().getName() + ", " + "wwaited...");
                notEmpty.await();
                System.out.println(Thread.currentThread().getName() + ", " + "notified...");
            }
            T object = queue.take();
            --count;
            notFull.signalAll();
            return object;
        }
        finally
        {
            lock.unlock();
        }
    }

    public void clear()
    {
        lock.lock();
        try
        {
            queue.clear();
        }
        finally
        {
            lock.unlock();
        }
    }

    public int size()
    {
        return queue.size();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
