package com.yk.bitcoin;

import com.yk.bitcoin.model.Chunk;
import com.yk.bitcoin.model.Task;
import com.yk.queue.BoundedBlockingQueue;
import lombok.Data;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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
                try
                {
                    TimeUnit.SECONDS.sleep(1);
                }
                catch (InterruptedException e)
                {
                }
                logger.debug("producer service pool status {}", producerService.isTerminated());
                logger.debug("consumer service pool status {}", consumerService.isTerminated());
            }
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
