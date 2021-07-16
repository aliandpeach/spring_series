package com.yk.bitcoin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/07/09 15:36:44
 */
public class ThreadInterrupt
{
    private static ExecutorService service = Executors.newFixedThreadPool(1);

    public static void main(String args[]) throws InterruptedException
    {
        List<Map<String, String>> list = new ArrayList<>();
        String str = list.stream().map(t -> t.get("a")).collect(Collectors.joining());
        System.out.println(str);

        AtomicInteger top = new AtomicInteger(0);
        service.submit(() ->
        {
            long start = System.currentTimeMillis();
            int num = top.get();
            while (num < 100000)
            {
                if (System.currentTimeMillis() - start > 30 * 1000)
                {
                    System.out.println(Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                }

                try
                {
                    TimeUnit.MILLISECONDS.sleep(3 * 1000);
                }
                catch (InterruptedException e)
                {
                    System.out.println(System.currentTimeMillis() - start);
                    e.printStackTrace();
                    break;
                }
                num = top.incrementAndGet();
            }
        });
        service.shutdown();
        service.awaitTermination(6000, TimeUnit.MILLISECONDS);
    }
}
