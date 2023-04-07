package com.yk.bitcoin.produce;

import cn.hutool.core.util.HexUtil;
import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.Context;
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

    public KeyGeneratorRunner(KeyGenerator generator,
                              Context context)
    {
        super(generator, context);
    }

    @Override
    public List<Key> createKey(int length)
    {
        try
        {
            context.getLock().lock();
            List<Key> result = new ArrayList<>();
            for (int i = 0; i < length; i++)
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
            context.getLock().unlock();
        }
    }

    private Key getKey()
    {
        if (null == context.getTask().getMin() || null == context.getTask().getMax())
        {
            return null;
        }

        byte[] byteKey;
        if (!(context.getTask().getMin().compareTo(context.getTask().getMax()) < 0))
        {
            return null;
        }
        try
        {
            byteKey = Utils.bigIntegerToBytes(context.getTask().getMin(), 32);
        }
        catch (RuntimeException e)
        {
            error.error("Utils.bigIntegerToBytes error : " + context.getTask().getMin().toString(16), e);
            return null;
        }

        String hex = HexUtil.encodeHexStr(byteKey);
        // 多线程同步打印
        hex_key.info(Thread.currentThread().getName() + "-current hex = " + hex + ", binary string = " + BinHexSHAUtil.bytes2BinaryString(byteKey));
        try
        {
            String prk = generator.keyGen(byteKey, true);
            String puk = generator.addressGen(byteKey, true);
            recordLogger.info(Thread.currentThread().getName() + ", " + prk + ", " + puk);
            context.getTask().setMin(context.getTask().getMin().add(one));
            return new Key(prk, puk);
        }
        catch (Exception e)
        {
            error.error("KeyGeneratorRunner private key generator keyGen error", e);
            return null;
        }
    }

    @Override
    public String getName()
    {
        return KeyGeneratorRunner.class.getName();
    }
}
