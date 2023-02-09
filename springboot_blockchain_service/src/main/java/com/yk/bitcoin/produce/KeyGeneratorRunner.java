package com.yk.bitcoin.produce;

import cn.hutool.core.util.HexUtil;
import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.KeyGenerator;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Key;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.Utils;
import com.yk.queue.BoundedBlockingQueue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

public class KeyGeneratorRunner extends AbstractKeyGenerator
{
    private final BigInteger one = new BigInteger("1", 16);

    private BigInteger min;

    private final BigInteger max;

    private final Lock lock;

    public KeyGeneratorRunner(KeyGenerator generator,
                              BlockchainProperties blockchainProperties,
                              BoundedBlockingQueue<Chunk> queue,
                              BlockingQueue<Chunk> retry,
                              BigInteger min,
                              BigInteger max, Lock lock)
    {
        super(generator, blockchainProperties, queue, retry);
        this.min = min;
        this.max = max;
        this.lock = lock;
    }

    @Override
    public List<Key> createKey()
    {
        try
        {
            lock.lock();
            List<Key> result = new ArrayList<>();
            for (int i = 0; i < blockchainProperties.getDataLen(); i++)
            {
                Key key = getKey();
                if (null == key)
                {
                    continue;
                }
                result.add(key);
            }
            return result;
        }
        finally
        {
            lock.unlock();
        }
    }

    private Key getKey()
    {
        if (null == min || null == max)
        {
            return null;
        }

        byte[] byteKey;
        if (!(min.compareTo(max) < 0))
        {
            return null;
        }
        try
        {
            byteKey = Utils.bigIntegerToBytes(min, 32);
        }
        catch (RuntimeException e)
        {
            error.error("Utils.bigIntegerToBytes error : " + min.toString(16), e);
            return null;
        }

        String hex = HexUtil.encodeHexStr(byteKey);
        // 多线程同步打印
        hex_key.info(Thread.currentThread().getName() + "-current hex = " + hex + ", binary string = " + BinHexSHAUtil.bytes2BinaryString(byteKey));
        try
        {
            String prk = generator.keyGen(byteKey, true);
            String puk = generator.addressGen(byteKey);
            recordLogger.info(Thread.currentThread().getName() + ", " + prk + ", " + puk);
            setMin(min.add(one));
            return new Key(prk, puk);
        }
        catch (Exception e)
        {
            error.error("KeyGeneratorRunner private key generator keyGen error", e);
            return null;
        }
    }

    public BigInteger getMin()
    {
        return min;
    }

    public void setMin(BigInteger min)
    {
        this.min = min;
    }

    @Override
    public String getName()
    {
        return KeyGeneratorRunner.class.getName();
    }
}
