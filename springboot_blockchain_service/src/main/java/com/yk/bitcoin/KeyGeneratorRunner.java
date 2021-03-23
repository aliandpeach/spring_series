package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class KeyGeneratorRunner implements Runnable
{
    private Logger status = LoggerFactory.getLogger("generator");
    
    private Logger record = LoggerFactory.getLogger("record");
    
    @Autowired
    private KeyGenerator generator;
    
    @Autowired
    private Cache cache;
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    private SecureRandom secure = new SecureRandom();
    
    private Random random = new Random();
    
    @Override
    public void run()
    {
        if (!cache.run || !blockchainProperties.isExecute())
        {
            status.info("KeyGeneratorRunner stopped!");
            return;
        }
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            status.error("private key generator sleep error", e);
        }
        while (KeyCache.keyQueue.size() > 0)
        {
            synchronized (KeyCache.lock)
            {
                try
                {
                    KeyCache.lock.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        try
        {
            for (int i = 0; i < blockchainProperties.getProduce(); i++)
            {
                byte[] keyBytes = new byte[32];
                if (blockchainProperties.isSecure())
                {
                    secure.nextBytes(keyBytes);
                }
                else
                {
                    random.nextBytes(keyBytes);
                }
                
                String prikey = generator.keyGen(keyBytes, true);
                String pubkey = generator.addressGen(keyBytes);
                record.info(prikey + ", " + pubkey);
                Map<String, String> keyAddr = new HashMap<>();
                keyAddr.put("privatekey", prikey);
                keyAddr.put("publickey", pubkey);
                KeyCache.keyQueue.offer(keyAddr);
            }
        }
        catch (Exception e)
        {
            status.error("private key generator keyGen error", e);
        }
        
        synchronized (KeyCache.lock)
        {
            KeyCache.lock.notifyAll();
        }
    }
}
