package com.yk.bitcoin;

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
    private Logger logger = LoggerFactory.getLogger("demo");
    
    @Autowired
    private KeyGeneratorRunner keyGeneratorRunner;
    
    @Autowired
    private KeyWatchedRunner keyWatchedRunner;
    
    
    @Async("executor")
    public void main()
    {
        logger.info("main running start " + System.currentTimeMillis());
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10, new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });
        
        service.scheduleAtFixedRate(keyGeneratorRunner, 0, 5, TimeUnit.SECONDS);
        
        ScheduledExecutorService watched = Executors.newScheduledThreadPool(10, new ThreadFactory()
        {
            private AtomicInteger integer = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-watched-" + integer.getAndIncrement());
            }
        });
        
        watched.scheduleAtFixedRate(keyWatchedRunner, 0, 5, TimeUnit.SECONDS);
        logger.info("main running end " + System.currentTimeMillis());
    }
}
