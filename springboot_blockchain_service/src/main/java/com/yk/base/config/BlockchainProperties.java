package com.yk.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * BlockchainProperties
 */
@Configuration
@ConfigurationProperties(prefix = "block.chain")
@Data
public class BlockchainProperties
{
    private int dataLen = 20;

    private String minKey = "1";

    private String maxKey = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    private String apiHost = "https://blockchain.info/balance";

    private int producer = 1;

    private int consumer = 1;
}
