package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.consume.KeyWatchedRunner;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Task;
import com.yk.bitcoin.produce.AbstractKeyGenerator;
import com.yk.bitcoin.produce.KeyGeneratorRunner;
import com.yk.queue.BoundedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableAsync
public class KeyGeneratorWatchedService
{
    private static final Logger logger = LoggerFactory.getLogger(KeyGeneratorWatchedService.class);

    @Autowired
    private BlockchainProperties blockchainProperties;

    @Autowired
    private KeyGenerator generator;


    public void main(Task task)
    {
        BoundedBlockingQueue<Chunk> queue = new BoundedBlockingQueue<>(new LinkedBlockingQueue<>(), 20);
        BlockingQueue<Chunk> retry = new LinkedBlockingQueue<>();
        Lock lock = new ReentrantLock();

        AtomicInteger integer = new AtomicInteger(0);
        logger.info("main running start " + System.currentTimeMillis());
        ScheduledExecutorService producer = Executors.newScheduledThreadPool(blockchainProperties.getProducer(), new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });

        AbstractKeyGenerator keyGeneratorRunner = new KeyGeneratorRunner(generator, blockchainProperties, queue, retry, task.getMin(), task.getMax(), lock);
        for (int i = 0; i < blockchainProperties.getProducer(); i++)
        {
            producer.scheduleWithFixedDelay(keyGeneratorRunner, 0, 1, TimeUnit.MILLISECONDS);
        }

        ScheduledExecutorService consumer = Executors.newScheduledThreadPool(blockchainProperties.getConsumer(), new ThreadFactory()
        {
            private final AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-watched-" + integer.getAndIncrement());
            }
        });

        KeyWatchedRunner keyWatchedRunner = new KeyWatchedRunner(task, queue, retry);
        for (int i = 0; i < blockchainProperties.getConsumer(); i++)
        {
            consumer.scheduleWithFixedDelay(keyWatchedRunner, 0, 1, TimeUnit.MILLISECONDS);
        }
        logger.info("main running end " + System.currentTimeMillis());
        task.setConsumerService(consumer);
        task.setProducerService(producer);
    }
}
