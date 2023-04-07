package com.yk.bitcoin;

import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Key;
import com.yk.bitcoin.model.Task;
import com.yk.queue.BoundedBlockingQueue;
import lombok.Data;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Data
public class Context
{
    private Task task;

    private int chunkDataLength;

    private BoundedBlockingQueue<Chunk> queue;

    private BlockingQueue<Chunk> retry;

    private Lock lock;

    private ExecutorService producerService;

    private ExecutorService consumerService;

    private Logger logger;

    public Context(Task task)
    {
        this.task = task;
    }

    public synchronized void updateTaskStart()
    {
        this.task.setState(1);
    }

    public synchronized void updateTaskStop()
    {
        this.task.setState(0);

        int count = 0;
        while (queue.size() > 0 || retry.size() > 0)
        {
            if (count > 20)
            {
                this.task.setState(-1); // 强制退出

                List<Key> retryKeys = new ArrayList<>();
                while (retry.size() > 0)
                {
                    Chunk _chunk = retry.poll();
                    Optional.ofNullable(_chunk).ifPresent(c -> retryKeys.addAll(c.getDataList()));
                }

                List<Key> queueKeys = new ArrayList<>();
                while (queue.size() > 0)
                {
                    Chunk _chunk = null;
                    try
                    {
                        _chunk = queue.take();
                    }
                    catch (InterruptedException e)
                    {
                    }
                    Optional.ofNullable(_chunk).ifPresent(c -> queueKeys.addAll(c.getDataList()));
                }
//                List<Key> _keys = retry.stream().map(Chunk::getDataList).flatMap(Collection::stream).collect(Collectors.toList());
                if (null != logger)
                    logger.debug("retry keys {}", retryKeys.stream().map(t -> t.getPrivateKey() + ", " + t.getPublicKey()).collect(Collectors.joining("\n")));
                if (null != logger)
                    logger.debug("queue keys {}", queueKeys.stream().map(t -> t.getPrivateKey() + ", " + t.getPublicKey()).collect(Collectors.joining("\n")));
                break;
            }
            sleep(1000);
            if (null != logger)
                logger.debug("queue size is {}, retry size is {}", queue.size(), retry.size());
            count++;
        }

        if (null != producerService && null != consumerService)
        {
            producerService.shutdown();
            consumerService.shutdown();

            if (null != logger)
            {
                logger.debug("producer service pool status {}", producerService.isTerminated());
                logger.debug("consumer service pool status {}", consumerService.isTerminated());
            }

            // 必须使用 shutdownNow, Generator线程在 context.getQueue().put(_chunk); 行时, 可能处于等待中
            producerService.shutdownNow();
            // 必须使用 shutdownNow, Watch线程在 Chunk chunk = context.getQueue().take() 行时, 可能处于等待中
            consumerService.shutdownNow();

            if (null != logger)
            {
                logger.debug("producer service pool status {}", producerService.isTerminated());
                logger.debug("consumer service pool status {}", consumerService.isTerminated());
                sleep(1000);
                logger.debug("producer service pool status {}", producerService.isTerminated());
                logger.debug("consumer service pool status {}", consumerService.isTerminated());
            }
        }
    }

    private void sleep(long millis)
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(millis);
        }
        catch (InterruptedException e)
        {
        }
    }

    public synchronized void updateTaskPause()
    {
        this.task.setState(2);
    }

    public int queryTaskStatus()
    {
        return task.getState();
    }
}
