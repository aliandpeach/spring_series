package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Task;
import com.yk.bitcoin.produce.AbstractKeyGenerator;
import com.yk.bitcoin.produce.KeyGeneratorRunner;
import com.yk.queue.BoundedBlockingQueue;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorTest
{
    public static void main(String args[]) throws InterruptedException
    {
        System.setProperty("log.home", System.getProperty("user.dir"));
        BoundedBlockingQueue<Chunk> queue = new BoundedBlockingQueue<>(new LinkedBlockingQueue<>(), 20);
        BlockingQueue<Chunk> retry = new LinkedBlockingQueue<>();
        Lock lock = new ReentrantLock();

        BigInteger min = new BigInteger("1");
        BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

        Context context = new Context(new Task(AbstractKeyGenerator.getKeyGeneratorName(0)));
        context.getTask().setMin(min);
        context.getTask().setMax(max);
        context.setQueue(queue);
        context.setRetry(retry);
        context.setLock(lock);
        context.setChunkDataLength(20);

        AtomicInteger integer = new AtomicInteger(0);
        AbstractKeyGenerator keyGeneratorRunner = new KeyGeneratorRunner(new KeyGenerator(), context);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });

        for (int i = 0; i < 2; i++)
        {
            executor.scheduleWithFixedDelay(keyGeneratorRunner, 0, 1, TimeUnit.SECONDS);
        }
        System.out.println();
        context.updateTaskStart();
        Thread.sleep(1000);
        executor.shutdown();
    }
}
