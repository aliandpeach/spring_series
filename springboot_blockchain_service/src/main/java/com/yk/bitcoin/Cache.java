package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

import static com.yk.bitcoin.KeyCache.LOCK;

@Service
public class Cache
{
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    private volatile boolean run = false;
    
    private volatile BigInteger min;
    
    private volatile BigInteger max;
    
    /*public Cache(BlockchainProperties blockchainProperties)
    {
        synchronized (LOCK)
        {
            min = new BigInteger(blockchainProperties.getMinKey(), 16);
            max = new BigInteger(blockchainProperties.getMaxKey(), 16);
        }
    }*/
    
    public synchronized boolean isRun()
    {
        return run;
    }
    
    public synchronized void setRun(boolean run)
    {
        this.run = run;
    }
    
    public BigInteger getMin()
    {
        return null == min ? new BigInteger(blockchainProperties.getMinKey(), 16) : min;
    }
    
    public void setMin(BigInteger min)
    {
        this.min = min;
    }
    
    public BigInteger getMax()
    {
        return null == max ? new BigInteger(blockchainProperties.getMaxKey(), 16) : max;
    }
    
    public void setMax(BigInteger max)
    {
        this.max = max;
    }
    
    @Bean
    public BigInteger min(BlockchainProperties blockchainProperties)
    {
        synchronized (LOCK)
        {
            return new BigInteger(blockchainProperties.getMinKey(), 16);
        }
    }
    
    @Bean
    public BigInteger max(BlockchainProperties blockchainProperties)
    {
        synchronized (LOCK)
        {
            return new BigInteger(blockchainProperties.getMaxKey(), 16);
        }
    }
}
