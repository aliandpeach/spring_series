package com.yk.index.service;

import com.yk.index.model.UploadChunkResponse;
import com.yk.index.model.WebUploadChunkRequest;
import com.yk.index.service.upload.strategy.UploadContext;
import com.yk.index.service.upload.strategy.UploadModeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service("videoService")
public class VideoServiceImpl implements ApplicationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final ExecutorService executorService = Executors.newFixedThreadPool(
            25, (r) ->
            {
                String threadName = "upload-pool-" + atomicInteger.getAndIncrement();
                Thread thread = new Thread(r);
                thread.setName(threadName);
                return thread;
            });

    private final CompletionService<UploadChunkResponse> completionService
            = new ExecutorCompletionService<>(executorService, new LinkedBlockingDeque<>(100));

    public UploadChunkResponse upload(WebUploadChunkRequest webUploadChunkRequest)
    {
        return UploadChunkResponse.builder().build();
    }

    public UploadChunkResponse sliceUpload(WebUploadChunkRequest uploadChunkRequest) throws ExecutionException, InterruptedException, IOException
    {
        // 进入线程会使得controller在线程没有结束前执行完毕, 则上传的临时文件会被删除(CompletionService 先返回先执行完的线程, 可能返回的是前面线程的结果)
        /*completionService.submit(() -> UploadContext.INSTANCE.getStrategyByType(UploadModeEnum.MAPPED_BYTEBUFFER).sliceUpload(uploadChunkRequest));
        return completionService.take().get();*/

        return UploadContext.INSTANCE.getStrategyByType(UploadModeEnum.MAPPED_BYTEBUFFER).sliceUpload(uploadChunkRequest);
    }

    @Override
    public void run(ApplicationArguments args)
    {

    }
}
