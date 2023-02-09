package com.yk.bitcoin;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.yk.bitcoin.model.Key;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RunnableTest2 implements Runnable
{
    public static final Set<String> r = new ConcurrentHashSet<>();

    private final AtomicInteger index;

    private final List<Key> dataList;

    private final AtomicInteger count;

    public RunnableTest2(AtomicInteger count, AtomicInteger index, List<Key> dataList)
    {
        this.count = count;
        this.index = index;
        this.dataList = dataList;
    }

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (this)
            {
                if (count.get() > 10000)
                {
                    System.out.println(dataList.stream().map(Key::getPrivateKey).collect(Collectors.joining(",")));
                    r.addAll(dataList.stream().map(Key::getPrivateKey).collect(Collectors.toSet()));
                    dataList.clear();
                    index.set(0);
                    System.out.println(Thread.currentThread() + " : " + r.size());
                    System.out.println(Thread.currentThread() + " : " + r.stream().map(Integer::valueOf).sorted(Comparator.comparing(t -> t)).map(t -> t + "").collect(Collectors.joining(",")).equals(k()));
                    break;
                }
                Key key = new Key(count + "", count + "");
                count.addAndGet(1);
                dataList.add(key);
                index.addAndGet(1);
                if (index.get() % 20 == 0)
                {
                    System.out.println(dataList.stream().map(Key::getPrivateKey).collect(Collectors.joining(",")));
                    Set<String> _t = dataList.stream().map(Key::getPrivateKey).collect(Collectors.toSet());
                    r.addAll(new HashSet<>(_t));
                    dataList.clear();
                    index.set(0);
                }
            }
        }
    }

    public static void main1(String[] args)
    {
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger index = new AtomicInteger(0);
        List<Key> dataList = new ArrayList<>();
        RunnableTest2 runnableTest = new RunnableTest2(count, index, dataList);
        for (int i = 0; i < 5; i++)
        {
            new Thread(() ->
            {
                while (true)
                {
                    runnableTest.run();
                }
            }).start();
        }
    }

    public static void main(String[] args)
    {
        AtomicInteger integer = new AtomicInteger(0);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "key-generator-" + integer.getAndIncrement());
            }
        });
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger index = new AtomicInteger(0);
        List<Key> dataList = new ArrayList<>();
        RunnableTest2 runnableTest = new RunnableTest2(count, index, dataList);
        for (int i = 0; i < 5; i++)
        {
            int kkk = new Random().nextInt(5 - 1 + 1) + 1;
            System.out.println(kkk);
            executor.scheduleWithFixedDelay(runnableTest, 0, kkk, TimeUnit.MILLISECONDS);
        }
        executor.shutdown();
    }

    private String k()
    {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 10000; i++)
        {
            list.add(i);
        }
        list.sort(Comparator.comparing(t -> t));
        return list.stream().sorted(Comparator.comparing(t -> t)).map(t -> t + "").collect(Collectors.joining(","));
    }
}
