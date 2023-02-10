package com.yk.bitcoin.produce;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.KeyCache;
import com.yk.bitcoin.KeyGenerator;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Key;
import com.yk.bitcoin.model.Task;
import com.yk.exception.BlockchainException;
import com.yk.queue.BoundedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

public abstract class AbstractKeyGenerator implements Runnable
{
    static final Logger recordLogger = LoggerFactory.getLogger("record");

    static final Logger error = LoggerFactory.getLogger("error");

    static final Logger status = LoggerFactory.getLogger("status");

    static final Logger hex_key = LoggerFactory.getLogger("hex_key");

    final KeyGenerator generator;

    final BlockchainProperties blockchainProperties;

    final BoundedBlockingQueue<Chunk> queue;

    final BlockingQueue<Chunk> retry;

    public AbstractKeyGenerator(KeyGenerator generator,
                                BlockchainProperties blockchainProperties,
                                BoundedBlockingQueue<Chunk> queue,
                                BlockingQueue<Chunk> retry)
    {
        this.generator = generator;
        this.blockchainProperties = blockchainProperties;
        this.queue = queue;
        this.retry = retry;
    }

    public abstract List<Key> createKey();

    public abstract String getName();

    public boolean queryTaskStatus()
    {
        return KeyCache.TASK_INFO.computeIfAbsent(this.getName(), t -> new Task(this.getName())).getState() == 0;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (queryTaskStatus() && retry.size() == 0)
            {
                status.info("{} stopped! current thread = {}", this.getName(), Thread.currentThread().getName());
                break;
            }
            try
            {
                Chunk _chunk = retry.poll();
                if (null != _chunk)
                {
                    queue.put(_chunk);
                    continue;
                }

                List<Key> keyList = createKey();
                if (null == keyList || keyList.isEmpty())
                {
                    continue;
                }

                Chunk chunk = new Chunk();
                chunk.getDataList().addAll(keyList);
                queue.put(chunk);
            }
            catch (Exception e)
            {
                error.error("{} private key generator keyGen error", this.getName(), e);
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                error.error("{} Thread.sleep error", this.getName(), e);
            }
        }
    }

    public static AbstractKeyGenerator createKeyGenerator(int type, KeyGenerator generator,
                                                          BlockchainProperties blockchainProperties,
                                                          BoundedBlockingQueue<Chunk> queue,
                                                          BlockingQueue<Chunk> retry,
                                                          BigInteger min,
                                                          BigInteger max, Lock lock)
    {
        AbstractKeyGenerator abstractKeyGenerator;
        switch (type)
        {
            case 0:
                abstractKeyGenerator = new KeyGeneratorRunner(generator, blockchainProperties, queue, retry, min, max, lock);
                break;
            case 1:
                abstractKeyGenerator = new RandomKeyGeneratorRunner(generator, blockchainProperties, queue, retry);
                break;
            default:
                throw new BlockchainException(0, "Unexpected value: " + type);
        }
        return abstractKeyGenerator;
    }

    public static String getKeyGeneratorName(int type)
    {
        String name;
        switch (type)
        {
            case 0:
                name = KeyGeneratorRunner.class.getName();
                break;
            case 1:
                name = RandomKeyGeneratorRunner.class.getName();
                break;
            default:
                throw new BlockchainException(0, "Unexpected value: " + type);
        }
        return name;
    }
}
