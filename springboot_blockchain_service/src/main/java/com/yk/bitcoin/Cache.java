package com.yk.bitcoin;

import com.yk.base.config.BlockchainProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class Cache
{
    private volatile boolean run = false;
    
    public synchronized boolean isRun()
    {
        return run;
    }
    
    public synchronized void setRun(boolean run)
    {
        this.run = run;
    }
    
    @Bean
    public BigInteger min(BlockchainProperties blockchainProperties)
    {
        return new BigInteger(blockchainProperties.getMinKey(), 16);
    }
    
    @Bean
    public BigInteger max(BlockchainProperties blockchainProperties)
    {
        return new BigInteger(blockchainProperties.getMaxKey(), 16);
    }
}
