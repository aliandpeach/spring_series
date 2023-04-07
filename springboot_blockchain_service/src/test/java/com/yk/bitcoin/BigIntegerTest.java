package com.yk.bitcoin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BigIntegerTest
{
    private static class CountRunner implements Runnable
    {
        private BigInteger count;
        private final Object lock;
        private Key key;

        public CountRunner(BigInteger count, Object lock)
        {
            this.count = count;
            this.lock = lock;
        }

        public CountRunner(Key key, Object lock)
        {
            this.key = key;
            this.lock = lock;
        }

        private void count() throws InterruptedException
        {
            synchronized (lock)
            {
                if (null != key)
                {
                    BigInteger _i = key.getCount().add(new BigInteger("1"));
                    key.setCount(_i);
                    System.out.println(Thread.currentThread().getName() + ", " + _i.intValue());
                    TimeUnit.SECONDS.sleep(1);
                }
                else if (null != count)
                {
                    count = count.add(new BigInteger("1"));
                    System.out.println(Thread.currentThread().getName() + ", " + count.intValue());
                    TimeUnit.SECONDS.sleep(3);
                }
            }
        }

        @Override
        public void run()
        {
            try
            {
                count();
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    private static class Key
    {
        @Getter
        @Setter
        private BigInteger count;

        public Key(BigInteger count)
        {
            this.count = count;
        }
    }

    public static void main2(String[] args) throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);
        BigInteger test = new BigInteger("1");
        Key key = new Key(test);
        Object lock = new Object();
        for (int i = 0; i < 3; i++)
        {
            CountRunner countRunner = new CountRunner(key, lock);
            executor.scheduleWithFixedDelay(countRunner, 0, 100, TimeUnit.MILLISECONDS);
        }
        TimeUnit.SECONDS.sleep(30);

        System.out.println(Thread.currentThread().getName() + ", " + test.intValue());
    }

    public static void main(String[] args) throws InterruptedException
    {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);
        BigInteger test = new BigInteger("1");
        Object lock = new Object();
        CountRunner countRunner = new CountRunner(test, lock);
        for (int i = 0; i < 3; i++)
        {
            executor.scheduleWithFixedDelay(countRunner, 0, 100, TimeUnit.MILLISECONDS);
        }
        TimeUnit.SECONDS.sleep(30);

        System.out.println(Thread.currentThread().getName() + ", " + test.intValue());
    }
}
