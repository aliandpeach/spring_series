package com.yk.bitcoin;

import cn.hutool.core.util.HexUtil;
import com.yk.base.config.BlockchainProperties;
import com.yk.crypto.BinHexSHAUtil;
import com.yk.crypto.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.yk.bitcoin.KeyCache.LOCK;

@Service
public class KeyGeneratorRunner implements Runnable
{
    private Logger recordLogger = LoggerFactory.getLogger("record");
    
    private Logger error = LoggerFactory.getLogger("error");
    
    private Logger status = LoggerFactory.getLogger("status");
    
    private Logger hex_key = LoggerFactory.getLogger("hex_key");
    
    @Autowired
    private KeyGenerator generator;
    
    @Autowired
    private Cache cache;
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    private final BigInteger zero = new BigInteger("0", 16);
    
    private final BigInteger one = new BigInteger("1", 16);
    
    private SecureRandom secure = new SecureRandom();
    
    private Random random = new Random();
    
    @Override
    public void run()
    {
        if (!cache.isRun() || !blockchainProperties.isExecute())
        {
            status.info("KeyGeneratorRunner stopped! " + "current thread = " + Thread.currentThread().getName());
            return;
        }
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            error.error("KeyGeneratorRunner Thread.sleep error", e);
        }
        while (KeyCache.keyQueue.size() > 0)
        {
            synchronized (LOCK)
            {
                try
                {
                    LOCK.wait();
                }
                catch (InterruptedException e)
                {
                    error.error("KeyGeneratorRunner KeyCache.LOCK error", e);
                }
            }
        }
        
        while (true)
        {
            if (KeyCache.keyQueue.size() >= blockchainProperties.getProduce())
            {
                break;
            }
            
            byte[] barray;
            synchronized (LOCK)
            {
                if (!(cache.getMin().compareTo(cache.getMax()) < 0))
                {
                    break;
                }
                try
                {
                    barray = Utils.bigIntegerToBytes(cache.getMin(), 32);
                }
                catch (RuntimeException e)
                {
                    error.error("Utils.bigIntegerToBytes error : " + cache.getMin().toString(16), e);
                    break;
                }

                String hex = HexUtil.encodeHexStr(barray);
                // 多线程同步打印
                hex_key.info(Thread.currentThread().getName() + "-current hex = " + hex + ", binary string = " + BinHexSHAUtil.bytes2BinaryString(barray));
                cache.setMin(cache.getMin().add(one));
            }
            byte[] key = new byte[32];
            
            System.arraycopy(barray, 0, key, key.length - barray.length, barray.length);
            try
            {
                String prk = generator.keyGen(key, true);
                String puk = generator.addressGen(key);
                recordLogger.info(Thread.currentThread().getName() + ", " + prk + ", " + puk);
                Map<String, String> keyAddr = new HashMap<>();
                keyAddr.put("privatekey", prk);
                keyAddr.put("publickey", puk);
                KeyCache.keyQueue.offer(keyAddr);
            }
            catch (Exception e)
            {
                error.error("KeyGeneratorRunner private key generator keyGen error", e);
            }
        }
//        try
//        {
//            for (int i = 0; i < blockchainProperties.getProduce(); i++)
//            {
//                byte[] keyBytes = new byte[32];
//                if (blockchainProperties.isSecure())
//                {
//                    secure.nextBytes(keyBytes);
//                }
//                else
//                {
//                    random.nextBytes(keyBytes);
//                }
//
//                String prikey = generator.keyGen(keyBytes, true);
//                String pubkey = generator.addressGen(keyBytes);
//                recordLogger.info(prikey + ", " + pubkey);
//                Map<String, String> keyAddr = new HashMap<>();
//                keyAddr.put("privatekey", prikey);
//                keyAddr.put("publickey", pubkey);
//                KeyCache.keyQueue.offer(keyAddr);
//            }
//        }
//        catch (Exception e)
//        {
//            error.error("KeyGeneratorRunner private key generator keyGen error", e);
//        }
        synchronized (LOCK)
        {
            LOCK.notifyAll();
        }
    }
}
