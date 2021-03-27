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
    
    private String minKey = "1";
    
    private String maxKey = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    private String apiHost = "https://blockchain.info/balance";
    
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
    
    public String getMinKey()
    {
        return minKey;
    }
    
    public void setMinKey(String minKey)
    {
        this.minKey = minKey;
    }
    
    public String getMaxKey()
    {
        return maxKey;
    }
    
    public void setMaxKey(String maxKey)
    {
        this.maxKey = maxKey;
    }

    public String getApiHost()
    {
        return apiHost;
    }

    public void setApiHost(String apiHost)
    {
        this.apiHost = apiHost;
    }
}
