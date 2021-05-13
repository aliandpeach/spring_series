package com.yk.performance;

import cn.hutool.core.util.NumberUtil;
import com.yk.base.config.BlockchainProperties;
import com.yk.httprequest.HttpFormDataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/performance")
@EnableConfigurationProperties(BlockchainProperties.class)
public class PerformanceController implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger("performance");

    private static List<String> FILES = new CopyOnWriteArrayList<>();

    @Autowired
    private BlockchainProperties blockchainProperties;

    private static Random random = new Random();

    @GetMapping("/test")
    @ResponseBody
    public List<HttpFormDataUtil.HttpResponse> performance(@RequestParam Map<String, String> params)
    {
        int _semaphore = null != params.get("sema") && NumberUtil.isNumber(params.get("sema")) ? Integer.parseInt(params.get("sema")) : 20;
        Semaphore semaphore = new Semaphore(_semaphore);
        if (null == params.get("count") || !NumberUtil.isNumber(params.get("count")))
        {
            return new ArrayList<>(Collections.singletonList(new HttpFormDataUtil.HttpResponse(400, "请加入请求数量 count")));
        }
        int _count = Integer.parseInt(params.get("count"));
        int _files = null != params.get("files") && NumberUtil.isNumber(params.get("files")) ? Integer.parseInt(params.get("files")) : 1;
        long start = System.currentTimeMillis();

        List<CompletableFuture<HttpFormDataUtil.HttpResponse>> futures = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(_count);
        IntStream.range(0, _count).forEach(t ->
        {
            CompletableFuture<HttpFormDataUtil.HttpResponse> future = CompletableFuture.supplyAsync(() ->
            {
                try
                {
                    final List<String> data = new ArrayList<>(_files);
                    IntStream.range(0, _files).forEach(f -> data.add(FILES.get(random.nextInt(FILES.size()))));
                    semaphore.acquire();
                    return sendFormData(data);
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
        logger.debug("总数" + _count + " 个请求, 同时异步执行" + _semaphore + "个, 完成时间  = {}", end - start);
        return result;
    }

    public HttpFormDataUtil.HttpResponse sendFormData(List<String> filePaths) throws Exception
    {
        String url = blockchainProperties.getUrl();
        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();

        FileInfos fileInfos = new FileInfos();
        Map<String, String> filePathMap = new HashMap<>();

        for (String filePath : filePaths){
            File file = new File(filePath);

            List<FileInfoParam> flist = new ArrayList<>();
            FileInfoParam info = new FileInfoParam();
            String fileId = UUID.randomUUID().toString().replace("-", "");
            info.setId(fileId);
            info.setName(file.getName());
            flist.add(info);
            fileInfos.setFileInfoParamList(flist);

            filePathMap.put(fileId, filePath);
        }

        String str;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             StringWriter writer = new StringWriter())
        {
            marshaller.marshal(fileInfos, outputStream);
//            marshaller.marshal(fileInfos, writer);
            str = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }

        String boundary = "" + UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormData(url, filePathMap, str, headers, false, boundary, "Content-Type: application/xml");
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
        for (File f : files)
        {
            if (f.isFile())
            {
                FILES.add(f.getCanonicalPath());
                continue;
            }
            listFiles(f);
        }
        logger.info("path {} is end load files", blockchainProperties.getPath());
    }

    public void listFiles(File dir) throws Exception
    {
        File[] files = dir.listFiles();
        if (null == files)
        {
            return;
        }
        for (File f : files)
        {
            if (f.isFile())
            {
                FILES.add(f.getCanonicalPath());
                continue;
            }
            listFiles(dir);
        }
    }
}
