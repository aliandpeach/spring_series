package com.yk.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * BlockchainProperties
 */
@Configuration
@ConfigurationProperties(prefix = "block.chain")
public class BlockchainProperties
{
    private boolean execute = false;
    
    private int consume = 20;
    
    private int produce = 1000;
    
    private boolean secure = false;
    
    public boolean isExecute()
    {
        return execute;
    }
    
    public void setExecute(boolean execute)
    {
        this.execute = execute;
    }
    
    public int getConsume()
    {
        return consume;
    }
    
    public void setConsume(int consume)
    {
        this.consume = consume;
    }
    
    public int getProduce()
    {
        return produce;
    }
    
    public void setProduce(int produce)
    {
        this.produce = produce;
    }
    
    public boolean isSecure()
    {
        return secure;
    }
    
    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }
}
