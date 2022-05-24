package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableAsync
public class KeyGeneratorWatchedService
{
    private Logger logger = LoggerFactory.getLogger(KeyGeneratorWatchedService.class);
    
    @Autowired
    private KeyGeneratorRunner keyGeneratorRunner;
    
    @Autowired
    private KeyWatchedRunner keyWatchedRunner;

    @Autowired
    private BlockchainProperties blockchainProperties;

    
    @Async("executor")
    public void main()
    {
        AtomicInteger integer = new AtomicInteger(0);
        logger.info("main running start " + System.currentTimeMillis());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(blockchainProperties.getProducer(), new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });

        for (int i = 0; i < blockchainProperties.getProducer(); i++)
        {
            executor.scheduleWithFixedDelay(keyGeneratorRunner, 0, 5, TimeUnit.SECONDS);
        }

        ScheduledExecutorService watched = Executors.newScheduledThreadPool(blockchainProperties.getConsumer(), new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(0);
        
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-watched-" + integer.getAndIncrement());
            }
        });

        for (int i = 0; i < blockchainProperties.getConsumer(); i++)
        {
            watched.scheduleWithFixedDelay(keyWatchedRunner, 0, 5, TimeUnit.SECONDS);
        }
        logger.info("main running end " + System.currentTimeMillis());
    }
}
