package com.yk.performance;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.yk.base.config.BlockchainProperties;
import com.yk.httprequest.HttpFormDataUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/performance")
@EnableConfigurationProperties(BlockchainProperties.class)
public class PerformanceController implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger("performance");

    private static List<File> FILES = new CopyOnWriteArrayList<>();
    private static final int SIZE = 1000;

    @Autowired
    private BlockchainProperties blockchainProperties;

    @Setter
    @Getter
    @Value("${block.chain.path}")
    private String path;

    @Setter
    @Getter
    @Value("${block.chain.url}")
    private String url;

    @Setter
    @Getter
    @Value("${block.chain.dev}")
    private boolean dev;

    private static Random random = new Random();

    @GetMapping("/test")
    @ResponseBody
    public List<HttpFormDataUtil.HttpResponse> performance(@RequestParam Map<String, String> params)
    {
        AtomicInteger counter = new AtomicInteger(0);

        int _semaphore = null != params.get("sema") && NumberUtil.isNumber(params.get("sema")) ? Integer.parseInt(params.get("sema")) : 20;
        Semaphore semaphore = new Semaphore(_semaphore);

        if (null == params.get("count") || !NumberUtil.isNumber(params.get("count")))
        {
            return new ArrayList<>(Collections.singletonList(new HttpFormDataUtil.HttpResponse(400, "请加入请求数量 count")));
        }
        int _total = Integer.parseInt(params.get("count"));
        int _files = null != params.get("files") && NumberUtil.isNumber(params.get("files")) ? Integer.parseInt(params.get("files")) : 1;
        long start = System.currentTimeMillis();

        logger.debug("_total={}", _total);
        logger.debug("sema={}", _semaphore);

        List<CompletableFuture<HttpFormDataUtil.HttpResponse>> futures = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(_semaphore * 2);
        BlockingQueue<Long> ttimes = new LinkedBlockingQueue<>();
        IntStream.range(0, _total).forEach(t ->
        {
            CompletableFuture<HttpFormDataUtil.HttpResponse> future = CompletableFuture.supplyAsync(() ->
            {
                try
                {
                    final List<File> data = new ArrayList<>(_files);
                    IntStream.range(0, _files).forEach(f -> data.add(FILES.get(random.nextInt(FILES.size()))));
                    semaphore.acquire();
                    HttpFormDataUtil.HttpResponse r = sendTest(data, counter, ttimes);
                    return r;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return new HttpFormDataUtil.HttpResponse(400, "error");
                }
                finally
                {
                    semaphore.release();
                }
            }, service);
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<HttpFormDataUtil.HttpResponse> result = new ArrayList<>();
        futures.forEach(f ->
        {
            try
            {
                result.add(f.get());
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        });
        long end = System.currentTimeMillis();
        logger.debug("总数" + _total + " 个请求, 同一时刻允许请求" + _semaphore + "个, 完成时间  = {}", end - start);
        return result;
    }

    @GetMapping("/test2")
    @ResponseBody
    public List<HttpFormDataUtil.HttpResponse> performance2(@RequestParam Map<String, String> params)
    {
        AtomicInteger counter = new AtomicInteger(0);

        int _reqCount = null != params.get("sema") && NumberUtil.isNumber(params.get("sema")) ? Integer.parseInt(params.get("sema")) : 20;

        if (null == params.get("count") || !NumberUtil.isNumber(params.get("count")))
        {
            return new ArrayList<>(Collections.singletonList(new HttpFormDataUtil.HttpResponse(400, "请加入请求数量 count")));
        }
        int _total = Integer.parseInt(params.get("count"));
        int _files = null != params.get("files") && NumberUtil.isNumber(params.get("files")) ? Integer.parseInt(params.get("files")) : 1;

        logger.debug("总请求数= {}", _total);
        logger.debug("分组内异步请求数量= {}", _reqCount);

        long globalStart = System.currentTimeMillis();

        ExecutorService service = Executors.newFixedThreadPool(_reqCount * 2);

        List<HttpFormDataUtil.HttpResponse> result = new CopyOnWriteArrayList<>();
        List<Group> all = new CopyOnWriteArrayList<>();

        int groupSize = _total % _reqCount == 0 ? _total / _reqCount : _total / _reqCount + 1;
        logger.debug("groupSize={}", groupSize);
        IntStream.range(0, groupSize).forEach(t ->
        {
            List<CompletableFuture<HttpFormDataUtil.HttpResponse>> futures = new ArrayList<>();
            BlockingQueue<Long> ttimes = new LinkedBlockingQueue<>();
            long start = System.currentTimeMillis();
            for (int i = 0; i < _reqCount; i++)
            {
                Executor executor = new Executor();
                executor.setCounter(counter);
                executor.setFileCount(_files);
                executor.setTtimes(ttimes);
                futures.add(CompletableFuture.supplyAsync(executor::call, service));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            futures.forEach(f ->
            {
                try
                {
                    result.add(f.get());
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }
            });
            long end = System.currentTimeMillis();
            logger.debug("分批执行请求，已经完成总数= " + counter.get() + ", " +
                    "此次使用时间= " + (end - start) + " , 单个请求平均时长= " + ttimes.stream().mapToDouble(Long::doubleValue).average().orElse(0.00d));

            Group group = new Group();
            group.getGroupEachTime().addAll(ttimes);
            group.setName("group-" + t);
            group.setUseTime((end - start));
            group.setStart(start);
            group.setEnd(end);
            all.add(group);
        });
        long globalEnd = System.currentTimeMillis();
        logger.debug("总数" + _total + " 个请求, 分批异步执行, 完成时间  = {}", globalEnd - globalStart);

        logger.debug("统计结果=============================");
        long startMin = all.stream().mapToLong(Group::getStart).min().getAsLong();
        long startMax = all.stream().mapToLong(Group::getStart).max().getAsLong();
        logger.debug((startMax - startMin) + " 毫秒内请求 " + (_total * _reqCount) + " 次");
        long groupMinUse = all.stream().mapToLong(Group::getUseTime).min().getAsLong();
        long groupMaxUse = all.stream().mapToLong(Group::getUseTime).max().getAsLong();
        logger.debug("分组完成" + _reqCount + " 次异步请求的最大用时= " + groupMaxUse
                + "平均时长为: " + all.stream().sorted(Comparator.comparing(Group::getUseTime).reversed()).findFirst().get().getGroupEachTime().stream().mapToLong(l -> l).average().getAsDouble());
        logger.debug("分组完成" + _reqCount + " 次异步请求的最小用时= " + groupMinUse
                + "平均时长为: " + all.stream().sorted(Comparator.comparing(Group::getUseTime)).findFirst().get().getGroupEachTime().stream().mapToLong(l -> l).average().getAsDouble());
        logger.debug("完成所有请求的平均用时= " + all.stream().flatMap(t -> t.getGroupEachTime().stream()).mapToLong(l -> l).average().getAsDouble());
        logger.debug("统计结果=============================");

        long success = result.stream().filter(r -> r.getCode() == 200).count();
        long failed = result.stream().filter(r -> r.getCode() != 200).count();
        logger.debug("成功的请求数量 = " + success + " 个, 失败的请求数量 = " + failed + " 个");
        return result;
    }

    /**
     * _total 个用户上传请求， 每个用户执行执行 sema 次任务
     *
     * @param params
     * @return
     */
    @GetMapping("/test2/user/async")
    @ResponseBody
    public List<HttpFormDataUtil.HttpResponse> performanceUserAsync(@RequestParam Map<String, String> params) throws InterruptedException
    {
        int _reqCount = null != params.get("sema") && NumberUtil.isNumber(params.get("sema")) ? Integer.parseInt(params.get("sema")) : 20;

        if (null == params.get("count") || !NumberUtil.isNumber(params.get("count")))
        {
            return new ArrayList<>(Collections.singletonList(new HttpFormDataUtil.HttpResponse(400, "请加入请求数量 count")));
        }
        int _total = Integer.parseInt(params.get("count"));
        int _files = null != params.get("files") && NumberUtil.isNumber(params.get("files")) ? Integer.parseInt(params.get("files")) : 1;
        boolean async = null == params.get("async") || "true".equals(params.get("async"));

        String s = async ? "异" : "同";
        logger.debug("用户数量= {}", _total);
        logger.debug("每个用户" + s + "步请求的数量= {}", _reqCount);
        logger.debug("总请求数量= {}", _total * _reqCount);

        List<HttpFormDataUtil.HttpResponse> result = new CopyOnWriteArrayList<>();

        ExecutorService users = Executors.newFixedThreadPool(_total);

        long _start = System.currentTimeMillis();

        List<Group> all = new CopyOnWriteArrayList<>();

        IntStream.range(1, _total + 1).forEach(index ->
        {
            users.submit(() ->
            {
                BlockingQueue<Long> ttimes = new LinkedBlockingQueue<>();
                long start = System.currentTimeMillis();
                AtomicInteger counter = new AtomicInteger(0);
                ExecutorService user = Executors.newFixedThreadPool(async ? _reqCount : 1);
                List<CompletableFuture<HttpFormDataUtil.HttpResponse>> futures = new ArrayList<>();
                for (int i = 0; i < _reqCount; i++)
                {
                    Executor executor = new Executor();
                    executor.setCounter(counter);
                    executor.setFileCount(_files);
                    executor.setTtimes(ttimes);
                    futures.add(CompletableFuture.supplyAsync(executor::call, user));
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                futures.forEach(f ->
                {
                    try
                    {
                        result.add(f.get());
                    }
                    catch (InterruptedException | ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                });
                long end = System.currentTimeMillis();
                logger.debug("用户" + index + "完成请求总数= " + counter.get() + "个, " +
                        "此次使用时间= " + (end - start) + " , 单个请求平均时长= " + ttimes.stream().mapToDouble(Long::doubleValue).average().orElse(0.00d));
                Group group = new Group();
                group.getGroupEachTime().addAll(ttimes);
                group.setName("user-" + index);
                group.setUseTime((end - start));
                group.setStart(start);
                group.setEnd(end);
                all.add(group);

            });
        });
        users.shutdown();
        users.awaitTermination(1, TimeUnit.DAYS);
        long _end = System.currentTimeMillis();
        logger.debug("用户数=" + _total + ", 每个用户异步执行" + _reqCount + "个请求, 使用时间= " + (_end - _start));

        long startMin = all.stream().mapToLong(Group::getStart).min().getAsLong();
        long startMax = all.stream().mapToLong(Group::getStart).max().getAsLong();
        logger.debug("统计结果=============================");
        logger.debug((startMax - startMin) + " 毫秒内请求 " + (_total * _reqCount) + " 次");
        long groupMinUse = all.stream().mapToLong(Group::getUseTime).min().getAsLong();
        long groupMaxUse = all.stream().mapToLong(Group::getUseTime).max().getAsLong();
        logger.debug("用户完成" + _reqCount + " 次异步请求的最大用时= " + groupMaxUse
                + "平均时长为: " + all.stream().sorted(Comparator.comparing(Group::getUseTime).reversed()).findFirst().get().getGroupEachTime().stream().mapToLong(l -> l).average().getAsDouble());
        logger.debug("用户完成" + _reqCount + " 次异步请求的最小用时= " + groupMinUse
                + "平均时长为: " + all.stream().sorted(Comparator.comparing(Group::getUseTime)).findFirst().get().getGroupEachTime().stream().mapToLong(l -> l).average().getAsDouble());
        logger.debug("所有用户完成请求的平均用时= " + all.stream().flatMap(t -> t.getGroupEachTime().stream()).mapToLong(l -> l).average().getAsDouble());
        logger.debug("统计结果=============================");
        long success = result.stream().filter(r -> r.getCode() == 200).count();
        long failed = result.stream().filter(r -> r.getCode() != 200).count();
        logger.debug("成功的请求数量 = " + success + " 个, 失败的请求数量 = " + failed + " 个");
        return result;
    }

    @GetMapping("/test/per/second")
    @ResponseBody
    public List<HttpFormDataUtil.HttpResponse> performancePerSecond(@RequestParam Map<String, String> params)
    {
        AtomicInteger counter = new AtomicInteger(0);

        int _semaphore = null != params.get("sema") && NumberUtil.isNumber(params.get("sema")) ? Integer.parseInt(params.get("sema")) : 20;
        RateLimiter limiter = RateLimiter.create(_semaphore);

        if (null == params.get("count") || !NumberUtil.isNumber(params.get("count")))
        {
            return new ArrayList<>(Collections.singletonList(new HttpFormDataUtil.HttpResponse(400, "请加入请求数量 count")));
        }
        int _total = Integer.parseInt(params.get("count"));
        int _files = null != params.get("files") && NumberUtil.isNumber(params.get("files")) ? Integer.parseInt(params.get("files")) : 1;
        long start = System.currentTimeMillis();

        logger.debug("总请求数量= {}", _total);
        logger.debug("每秒请求次数= {}", _semaphore);

        List<CompletableFuture<HttpFormDataUtil.HttpResponse>> futures = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(_total);

        List<Group> all = new CopyOnWriteArrayList<>();

        BlockingQueue<Long> ttimes = new LinkedBlockingQueue<>();
        IntStream.range(0, _total).forEach(t ->
        {
            CompletableFuture<HttpFormDataUtil.HttpResponse> future = CompletableFuture.supplyAsync(() ->
            {
                try
                {
                    limiter.acquire(1);
                    long reqStart = System.currentTimeMillis();
                    final List<File> data = new ArrayList<>(_files);
                    IntStream.range(0, _files).forEach(f -> data.add(FILES.get(random.nextInt(FILES.size()))));
                    HttpFormDataUtil.HttpResponse r = sendTest(data, counter, ttimes);
//                    if (ttimes.size() == _semaphore)
//                    {
//                        synchronized (ttimes)
//                        {
//                            if (ttimes.size() == _semaphore)
//                            {
//                                double average = ttimes.stream().mapToDouble(Long::doubleValue).average().orElse(0.000d);
//                                logger.debug("每秒请求数量= " + _semaphore + " , 完成请求平均时长= " + average);
//                                ttimes.clear();
//                            }
//                        }
//                    }
                    long reqEnd = System.currentTimeMillis();
                    Group group = new Group();
                    group.setName("request-" + r.getNumber());
                    group.setUseTime((reqEnd - reqStart));
                    group.setStart(reqStart);
                    group.setEnd(reqEnd);
                    all.add(group);
                    return r;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return new HttpFormDataUtil.HttpResponse(400, "error");
                }
            }, service);
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<HttpFormDataUtil.HttpResponse> result = new ArrayList<>();
        futures.forEach(f ->
        {
            try
            {
                result.add(f.get());
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        });
        long end = System.currentTimeMillis();
        logger.debug("总数" + _total + " 个请求, 每秒请求" + _semaphore + "个, 完成时间  = {}", end - start);

        logger.debug("结果统计========================");
        long startMin = all.stream().mapToLong(Group::getStart).min().getAsLong();
        long startMax = all.stream().mapToLong(Group::getStart).max().getAsLong();
        logger.debug((startMax - startMin) + " 毫秒内请求" + _total + " 个, 完成请求的平均时间为" + all.stream().mapToLong(Group::getUseTime).average().getAsDouble());
        logger.debug("最大请求返回时长为： " + all.stream().mapToLong(Group::getUseTime).max().getAsLong());
        logger.debug("最小请求返回时长为： " + all.stream().mapToLong(Group::getUseTime).min().getAsLong());
        logger.debug("结果统计========================");

        long success = result.stream().filter(r -> r.getCode() == 200).count();
        long failed = result.stream().filter(r -> r.getCode() != 200).count();
        logger.debug("成功的请求数量 = " + success + " 个, 失败的请求数量 = " + failed + " 个");
        return result;
    }

    private class Executor implements Callable<HttpFormDataUtil.HttpResponse>
    {
        private AtomicInteger counter;
        private int fileCount;
        private BlockingQueue<Long> ttimes;

        public void setTtimes(BlockingQueue<Long> ttimes)
        {
            this.ttimes = ttimes;
        }

        public void setFileCount(int fileCount)
        {
            this.fileCount = fileCount;
        }

        public void setCounter(AtomicInteger counter)
        {
            this.counter = counter;
        }

        @Override
        public HttpFormDataUtil.HttpResponse call()
        {
            try
            {
                final List<File> data = new ArrayList<>(fileCount);
                IntStream.range(0, fileCount).forEach(f -> data.add(FILES.get(random.nextInt(FILES.size()))));
                HttpFormDataUtil.HttpResponse r = sendTest(data, counter, ttimes);
                return r;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return new HttpFormDataUtil.HttpResponse(400, "error");
            }
        }
    }

    public HttpFormDataUtil.HttpResponse sendTest(List<File> files, AtomicInteger counter, BlockingQueue<Long> ttimes) throws Exception
    {
        if (blockchainProperties.isDev())
        {
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(new Random().nextInt(12 - 3 + 1) + 3);
            HttpFormDataUtil.HttpResponse r = new HttpFormDataUtil.HttpResponse(200, "SUCCESS");
            r.setNumber(counter.incrementAndGet());

            long end = System.currentTimeMillis();
            long cost = end - start;
            logger.debug("request-" + r.getNumber() + " start at: " + start + " , end at: " + end + " , use time= " + cost);
            ttimes.put(cost);
            return r;
        }
        return sendFormData(files, counter, ttimes);
    }

    public HttpFormDataUtil.HttpResponse sendFormData(List<File> files, AtomicInteger counter, BlockingQueue<Long> ttimes) throws Exception
    {
        String url = blockchainProperties.getUrl();
        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();

        FileInfos fileInfos = new FileInfos();
        Map<String, String> filePathMap = new HashMap<>();

        List<String> fnames = files.stream().map(f ->
        {
            try
            {
                return f.getCanonicalPath();
            }
            catch (IOException e)
            {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        for (File file : files)
        {
            List<FileInfoParam> flist = new ArrayList<>();
            FileInfoParam info = new FileInfoParam();
            String fileId = UUID.randomUUID().toString().replace("-", "");
            info.setId(fileId);
            info.setName(file.getName());
            flist.add(info);
            fileInfos.setFileInfoParamList(flist);

            filePathMap.put(fileId, file.getCanonicalPath());
        }

        String str;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             StringWriter writer = new StringWriter())
        {
            marshaller.marshal(fileInfos, outputStream);
//            marshaller.marshal(fileInfos, writer);
            str = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }

        long start = System.currentTimeMillis();
        String boundary = "" + UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormDataByHttpClient(url, filePathMap, str, headers, null, boundary, "application/xml");
        response.setNumber(counter.incrementAndGet());
        long end = System.currentTimeMillis();
        long cost = end - start;
        logger.debug("request-" + response.getNumber() + " start at: " + start + " , end at: " + end + " , use time= " + cost + " , file name= " + fnames);
        ttimes.put(cost);
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        File dir = new File(blockchainProperties.getPath());
        if (!dir.exists())
        {
            logger.error("path {} is not exists!!!", blockchainProperties.getPath());
            return;
        }
        File[] files = dir.listFiles();
        if (null == files)
        {
            logger.error("path {} is empty!!!", blockchainProperties.getPath());
            return;
        }

        logger.info("path {} is starting load files", blockchainProperties.getPath());
        try
        {
            List<File> loopFiles = FileUtil.loopFiles(blockchainProperties.getPath());
            FILES.addAll(loopFiles);
        }
        catch (Error r)
        {
            r.printStackTrace();
            logger.info("loopFiles error", r);
        }
        logger.info("path {} is end load files count= " + FILES.size(), blockchainProperties.getPath());
    }
}