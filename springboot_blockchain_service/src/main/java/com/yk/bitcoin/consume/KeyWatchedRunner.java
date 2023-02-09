package com.yk.bitcoin.consume;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.KeyCache;
import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Key;
import com.yk.bitcoin.model.Task;
import com.yk.httprequest.HttpClientUtil;
import com.yk.queue.BoundedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;


public class KeyWatchedRunner implements Runnable
{
    private static final Logger watchedLogger = LoggerFactory.getLogger("watched");
    
    private static final Logger status = LoggerFactory.getLogger("status");

    private static final Logger error = LoggerFactory.getLogger("error");
    
    private BlockchainProperties blockchainProperties;

    private HttpClientUtil httpClientUtil;

    private final Task task;

    private final BoundedBlockingQueue<Chunk> queue;

    private final BlockingQueue<Chunk> retry;

    public KeyWatchedRunner(Task task,
                            BoundedBlockingQueue<Chunk> queue,
                            BlockingQueue<Chunk> retry)
    {
        this.task = task;
        this.queue = queue;
        this.retry = retry;
    }

    @Override
    public void run()
    {
        boolean stopped = KeyCache.TASK_INFO.computeIfAbsent(task.getName(), t -> new Task(task.getName())).getState() == 0;
        if (stopped && queue.size() == 0)
        {
            status.info("KeyWatchedRunner stopped! " + "current thread = " + Thread.currentThread().getName());
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
            Map<String, String> params = new HashMap<>();
            Chunk chunk = queue.take();
            Map<String, String> temp = chunk.getDataList().stream()
                    .collect(Collectors.toMap(Key::getPublicKey, Key::getPrivateKey, (k1, k2) -> k1));

            List<Map<String, String>> dataList = new ArrayList<>();
            Map<String, String> map = dataList.stream().flatMap(t -> t.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));


            String publicKeys = chunk.getDataList().stream().map(Key::getPublicKey).collect(Collectors.joining(","));
            params.put("active", publicKeys);

            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            Map<String, Map<String, Long>> result = null;
            try
            {
                result = httpClientUtil.get(blockchainProperties.getApiHost(),
                        headers, params, new TypeReference<Map<String, Map<String, Long>>>()
                        {
                        }, 3);
            }
            catch (Exception e)
            {
                error.error("KeyWatchedRunner HttpClientUtil.get error", e);
            }

            if (result == null)
            {
                chunk.getDataList().forEach(k ->
                {
                    error.error("error : " + k.getPrivateKey() + ", " + k.getPublicKey());
                });
                retry.offer(chunk);
            }
            Optional.ofNullable(result).orElse(new HashMap<>()).forEach((pub, values) ->
            {
                long balance = values.get("final_balance");
                if (balance > 0)
                {
                    watchedLogger.info("Wallet private key = " + temp.get(pub) + ", public key=" + pub + ", balance: " + balance);
                }
            });
        }
        catch (Exception e)
        {
            error.error("KeyWatchedRunner error", e);
        }
    }
}
