package com;

import com.yk.util.ConvertUtil;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WaitNotifyTest
{
    @Test
    public void put() throws InterruptedException
    {
        String str1 = ".fsdfdf.";
        int _index = str1.lastIndexOf(".");
        String na = str1.substring(0, _index);
        na = str1.substring(_index + 1);


        String string = ".aa";
//        if (StringUtils.isEmpty(string) || string.trim().length() == 0)
//        {
//            return;
//        }
//        if (string.lastIndexOf(".") == -1
//                || string.lastIndexOf(".") == string.length() - 1)
//        {
//            return;
//        }
        String db = string.substring(0, string.lastIndexOf("."));
        String table = string.substring(string.lastIndexOf(".") + 1);

        List<Integer> list = Arrays.stream(IntStream.range(0, 10).toArray()).boxed().collect(Collectors.toList());
        List<List<Integer>> result = ConvertUtil.partition(list, 3);
        result = ListUtils.partition(list, 3);
        result = ConvertUtil.partition2(list, 3);

        Map<String, String> test = new ConcurrentHashMap<>();
//        test.put("c", "1");
//        test.put("a", "1");
//        test.put("b", "1");
        String k1 = test.putIfAbsent("a", "a"); // 没有就插入数据, 返回null; 有就不插入, 返回已有数据
        String k2 = test.computeIfPresent("b", (t, t2) -> "b"); // 有就插入覆盖数据, 返回插入的数据; 没有就不操作, 返回null
        String k3 = test.computeIfAbsent("c", t -> "c"); // 没有就插入数据, 返回插入的数据; 有就不插入, 返回已有数据
        String k4 = test.compute("d", (a, b) -> "d");
        System.out.println(test);


        System.out.println(9 % 1);
        System.out.println(10 % 11);
        System.out.println(11 % 11);
        System.out.println(21 % 11);
        System.out.println(29 % 11);
        System.out.println(54 % 11);

        System.out.println();
        System.out.println(".fsf".lastIndexOf("."));
        System.out.println("fsf".lastIndexOf("."));

        String str = ".fsf";
        int in = str.lastIndexOf(".");
        System.out.println(str.substring(0, in > 0 ? in : str.length()));
        System.out.println(str.substring(in + 1));
    }

    @Test
    public void test() throws InterruptedException
    {
        List<String> locks = new ArrayList<>();
        IntStream.range(0, 5).forEach(i ->
        {
            String lock = UUID.randomUUID().toString().replace("-", "");
            locks.add(lock);
        });
        locks.forEach(l ->
        {
            new Thread(() ->
            {
                long start = System.currentTimeMillis();
                final String lock = l.intern();
                synchronized (lock)
                {

                    try
                    {
                        lock.wait(8 * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                long end = System.currentTimeMillis();
                if (end - start >= 8 * 1000)
                {
                    System.out.println(l + " timeout " + (end - start));
                }
                else
                {
                    System.out.println(l + " notify!!! " + (end - start));
                }
            }).start();
        });
        TimeUnit.SECONDS.sleep(2);
        new Thread(() ->
        {
            String lock = locks.get(new Random().nextInt(5)).intern();
            System.out.println("notify " + lock);
            synchronized (lock)
            {
                lock.notifyAll();
            }
        }).start();
        new Thread(() ->
        {
            String lock = locks.get(new Random().nextInt(5)).intern();
            synchronized (lock)
            {
                lock.notifyAll();
            }
            System.out.println("notify " + lock);
        }).start();
        Thread.currentThread().join();
    }

    @Test
    public void testParkNanos() throws InterruptedException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(600);
        for (int i = 0; i < 600; i++)
        {
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        LockSupport.parkNanos(1);
                    }
                }
            });
        }
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
