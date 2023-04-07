package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.consume.KeyWatchedRunner;
import com.yk.bitcoin.model.Task;
import com.yk.bitcoin.produce.AbstractKeyGenerator;
import com.yk.bitcoin.produce.KeyGeneratorRunner;
import com.yk.httprequest.HttpClientUtil;
import com.yk.queue.BoundedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.yk.bitcoin.KeyCache.TASK_CONTEXT;

@Component
@EnableAsync
public class KeyGeneratorWatchedService
{
    private static final Logger logger = LoggerFactory.getLogger(KeyGeneratorWatchedService.class);

    @Autowired
    private BlockchainProperties blockchainProperties;

    @Autowired
    private KeyGenerator generator;

    @Autowired
    private HttpClientUtil httpClientUtil;


    public void stop(Task task)
    {
        Context context = TASK_CONTEXT.get(task);
        if (null == context)
        {
            return;
        }
        context.updateTaskStop();
        TASK_CONTEXT.remove(task);
    }

    public void start(Task task)
    {
        Context context = new Context(task);
        context.setQueue(new BoundedBlockingQueue<>(new LinkedBlockingQueue<>(), 20));
        context.setRetry(new LinkedBlockingQueue<>());
        context.setLock(new ReentrantLock());
        context.setChunkDataLength(blockchainProperties.getDataLen());
        context.setLogger(logger);
        TASK_CONTEXT.put(task, context);

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

        ScheduledExecutorService consumer = Executors.newScheduledThreadPool(blockchainProperties.getConsumer(), new ThreadFactory()
        {
            private final AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-watched-" + integer.getAndIncrement());
            }
        });

        context.setConsumerService(consumer);
        context.setProducerService(producer);

        AbstractKeyGenerator keyGeneratorRunner = AbstractKeyGenerator.createKeyGenerator(task.getType(), generator, context);
        for (int i = 0; i < blockchainProperties.getProducer(); i++)
        {
            producer.scheduleWithFixedDelay(keyGeneratorRunner, 0, 1, TimeUnit.MILLISECONDS);
        }

        KeyWatchedRunner keyWatchedRunner = new KeyWatchedRunner(context, blockchainProperties, httpClientUtil);
        for (int i = 0; i < blockchainProperties.getConsumer(); i++)
        {
            consumer.scheduleWithFixedDelay(keyWatchedRunner, 0, 1, TimeUnit.MILLISECONDS);
        }
        context.updateTaskStart();
        logger.info("main running end " + System.currentTimeMillis());
    }
}
