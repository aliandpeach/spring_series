package com.yk.bitcoin.produce;

import cn.hutool.core.util.HexUtil;
import com.yk.bitcoin.Context;
import com.yk.bitcoin.KeyGenerator;
import com.yk.bitcoin.model.Key;
import com.yk.crypto.BinHexSHAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomKeyGeneratorRunner extends AbstractKeyGenerator
{

    private final Random random = new Random();

    public RandomKeyGeneratorRunner(KeyGenerator generator,
                                    Context context)
    {
        super(generator, context);
    }

    @Override
    public List<Key> createKey(int length)
    {
        List<Key> result = new ArrayList<>();
        for (int k = 0; k < length; k++)
        {
            StringBuilder randomBinaryKeyString = new StringBuilder();
            for (int i = 0; i < 256; i++)
            {
                randomBinaryKeyString.append((random.nextInt(100 + 1 - 1) + 1) % 2 == 0 ? "0" : "1");
            }
            byte[] byteKey = BinHexSHAUtil.binaryString2bytes(randomBinaryKeyString.toString());

            String hex = HexUtil.encodeHexStr(byteKey);
            // 多线程同步打印
            hex_key.info(Thread.currentThread().getName() + "-current hex = " + hex + ", binary string = " + randomBinaryKeyString);

            String pri = generator.keyGen(byteKey, true);
            String puk = generator.addressGen(byteKey, true);
            recordLogger.info(Thread.currentThread().getName() + ", " + pri + ", " + puk);
            result.add(new Key(pri, puk));
        }
        return result;
    }

    @Override
    public String getName()
    {
        return RandomKeyGeneratorRunner.class.getName();
    }
}
