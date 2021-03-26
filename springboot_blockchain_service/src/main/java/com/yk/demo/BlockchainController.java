package com.yk.demo;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.yk.bitcoin.KeyCache.LOCK;

@Controller
@RequestMapping("/block/chain")
@EnableConfigurationProperties(BlockchainProperties.class)
public class BlockchainController
{
    @Autowired
    private Cache cache;
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    @RequestMapping(value = "/{status}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, String> opt(@PathVariable("status") String status, @RequestBody Map<String, String> body)
    {
        if (!blockchainProperties.isExecute())
        {
            return new HashMap<>(Collections.singletonMap("status", "un-execute"));
        }
        if (null == status || !status.equalsIgnoreCase("start"))
        {
            cache.setRun(false);
            return new HashMap<>(Collections.singletonMap("status", "stopped"));
        }
        String min = body.get("min");
        String max = body.get("max");
        synchronized (LOCK)
        {
            cache.setMin(new BigInteger(min, 16));
            cache.setMax(new BigInteger(max, 16));
            cache.setRun(status.equalsIgnoreCase("start"));
        }
        return new HashMap<>(Collections.singletonMap("status", "started"));
    }
}
