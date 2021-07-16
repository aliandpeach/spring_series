package com.yk.bitcoin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.base.config.BlockchainProperties;
import com.yk.httprequest.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.yk.bitcoin.KeyCache.LOCK;

@Service
public class KeyWatchedRunner implements Runnable
{
    private Logger watchedLogger = LoggerFactory.getLogger("watched");
    
    private Logger status = LoggerFactory.getLogger("status");

    private Logger error = LoggerFactory.getLogger("error");
    
    @Autowired
    private Cache cache;
    
    @Autowired
    private BlockchainProperties blockchainProperties;

    @Override
    public void run()
    {
        if ((!cache.isRun() || !blockchainProperties.isExecute()) && KeyCache.keyQueue.size() <= 0)
        {
            status.info("KeyWatchedRunner stopped! " + "current thread = " + Thread.currentThread().getName());
            synchronized (LOCK)
            {
                LOCK.notifyAll();
            }
            return;
        }
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            error.error("KeyWatchedRunner sleep error", e);
        }
        try
        {
            synchronized (LOCK)
            {
                while (KeyCache.keyQueue.size() <= 0)
                {
                    try
                    {
                        LOCK.wait();
                    }
                    catch (InterruptedException e)
                    {
                        error.error("KeyWatchedRunner KeyCache.LOCK error", e);
                    }
                }
            }
            Map<String, String> params = new HashMap<>();
            Map<String, String> temp = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < blockchainProperties.getConsume(); i++)
            {
                Map<String, String> keyMap = KeyCache.keyQueue.poll();
                if (keyMap == null)
                {
                    break;
                }
                String prikey = keyMap.get("privatekey");
                String pubkey = keyMap.get("publickey");
                sb.append(pubkey).append(",");

                temp.put(pubkey, prikey);
            }
            params.put("active", sb.toString().endsWith(",") ? sb.toString().substring(0, sb.toString().length() - 1) : sb.toString());

            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            Map<String, Map<String, Long>> result = null;
            try
            {
                result = HttpClientUtil.get(blockchainProperties.getApiHost()
                        , headers, params, new TypeReference<Map<String, Map<String, Long>>>()
                        {
                        }, 3);
            }
            catch (Exception e)
            {
                error.error("KeyWatchedRunner HttpClientUtil.get error", e);
            }

            if (result == null)
            {
                temp.forEach((pub, pri) ->
                {
                    error.error("error : " + pub + ", " + pri);
                    Map<String, String> map = new HashMap<>();
                    map.put("privatekey", pri);
                    map.put("publickey", pub);
                    KeyCache.keyQueue.offer(map);
                });
            }
            Optional.ofNullable(result).orElse(new HashMap<>()).entrySet().forEach(entry ->
            {
                String pub = entry.getKey();
                Map<String, Long> values = entry.getValue();
                long balance = values.get("final_balance");
                if (balance > 0)
                {
                    watchedLogger.info("Wallet private key = " + temp.get(pub) + ", public key=" + pub + ", balance: " + balance);
                }
            });

            synchronized (LOCK)
            {
                LOCK.notifyAll();
            }
        }
        catch (Exception e)
        {
            error.error("KeyWatchedRunner error", e);
        }
    }
}
