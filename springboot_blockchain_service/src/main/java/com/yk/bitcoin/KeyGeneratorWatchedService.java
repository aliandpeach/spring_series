package com.yk.bitcoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class KeyGeneratorWatchedService
{
    @Autowired
    private KeyGeneratorRunner keyGeneratorRunner;

    @Autowired
    private KeyWatchedRunner keyWatchedRunner;


    public void main()
    {
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
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        this.main();
    }
}
