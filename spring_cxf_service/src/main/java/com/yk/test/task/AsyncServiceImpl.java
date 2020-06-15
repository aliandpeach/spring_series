package com.yk.test.task;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public class AsyncServiceImpl
{
    @Async("asyncExecutorPool")
    public void orderTaskV1()
    {
        try
        {
            TimeUnit.SECONDS.sleep(12);
            System.out.println("orderTaskV1s");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Async("asyncExecutorPool")
    public void orderTaskV2()
    {
        try
        {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("orderTaskV2s");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Bean
    public Executor asyncExecutorPool()
    {
        return Executors.newFixedThreadPool(3);
    }
}
