package com.yk.bitcoin.produce;

import com.yk.bitcoin.Context;
import com.yk.bitcoin.KeyGenerator;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Key;
import com.yk.exception.BlockchainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractKeyGenerator implements Runnable
{
    static final Logger recordLogger = LoggerFactory.getLogger("record");

    static final Logger error = LoggerFactory.getLogger("error");

    static final Logger status = LoggerFactory.getLogger("status");

    static final Logger hex_key = LoggerFactory.getLogger("hex_key");

    final KeyGenerator generator;

    final Context context;

    public AbstractKeyGenerator(KeyGenerator generator, Context context)
    {
        this.generator = generator;
        this.context = context;
    }

    public abstract List<Key> createKey(int length);

    public abstract String getName();

    public boolean stopped()
    {
        return context.queryTaskStatus() == 0;
    }

    public boolean forceStopped()
    {
        return context.queryTaskStatus() == -1;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (forceStopped())
            {
                status.info("{} force stopped! current thread = {}", this.getName(), Thread.currentThread().getName());
                break;
            }
            if (stopped() && context.getRetry().size() == 0)
            {
                status.info("{} stopped! current thread = {}", this.getName(), Thread.currentThread().getName());
                break;
            }
            try
            {
                Chunk _chunk = context.getRetry().poll();
                if (null != _chunk)
                {
                    context.getQueue().put(_chunk);
                    continue;
                }

                if (stopped())
                {
                    break;
                }
                List<Key> keyList = createKey(context.getChunkDataLength());
                if (null == keyList || keyList.isEmpty())
                {
                    continue;
                }

                Chunk chunk = new Chunk();
                chunk.getDataList().addAll(keyList);
                context.getQueue().put(chunk);
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

    public static AbstractKeyGenerator createKeyGenerator(int type,
                                                          KeyGenerator generator,
                                                          Context context)
    {
        AbstractKeyGenerator abstractKeyGenerator;
        switch (type)
        {
            case 0:
                abstractKeyGenerator = new KeyGeneratorRunner(generator, context);
                break;
            case 1:
                abstractKeyGenerator = new RandomKeyGeneratorRunner(generator, context);
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
