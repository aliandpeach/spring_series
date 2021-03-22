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
    
    public boolean isExecute()
    {
        return execute;
    }
    
    public void setExecute(boolean execute)
    {
        this.execute = execute;
    }
}
