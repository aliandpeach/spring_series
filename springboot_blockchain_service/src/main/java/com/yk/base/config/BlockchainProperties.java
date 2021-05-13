package com.yk.base.config;

import lombok.Getter;
import lombok.Setter;
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

    private int producer = 1;

    private int consumer = 1;

    @Getter
    @Setter
    private String path = "";

    @Getter
    @Setter
    private String url = "";
    
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

    public int getProducer()
    {
        return producer;
    }

    public void setProducer(int producer)
    {
        this.producer = producer;
    }

    public int getConsumer()
    {
        return consumer;
    }

    public void setConsumer(int consumer)
    {
        this.consumer = consumer;
    }
}
