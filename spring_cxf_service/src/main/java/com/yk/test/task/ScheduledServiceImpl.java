package com.yk.test.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling//开启定时任务
@EnableAsync//开启异步线程
public class ScheduledServiceImpl
{
    @Scheduled(cron = "0 32 23 * * ? ")//每天23点32分   零点 :  0 0 0 * * ?
    @Async //防止该方法时间太久 阻塞下面比较迟的方法 还可以把方法放入线程池 AsyncServiceImpl
    public void orderTaskV1()
    {
        try
        {
            System.out.println("orderTaskV1s");
            TimeUnit.HOURS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 33 23 * * ? ")//每天23点33分   一点 : 0 0 1 * * ?
    @Async //防止上面的方法执行太久 阻塞这个方法
    public void orderTaskV2()
    {
        System.out.println("orderTaskV2s");
    }
}
