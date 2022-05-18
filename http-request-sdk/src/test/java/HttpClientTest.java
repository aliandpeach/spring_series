import com.yk.connector.http.HttpRequest;
import com.yk.connector.http.ProxyHost;
import com.yk.connector.sftp.FtpRequest;
import com.yk.core.FileInfo;
import com.yk.core.PropertyLoader;
import com.yk.core.Response;
import com.yk.core.SdkExecutors;
import com.yk.core.CommonInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class HttpClientTest
{
    @Before
    public void before() throws IOException
    {
        System.setProperty("limit", "false");
        SdkExecutors.create().init(PropertyLoader.loadProperties());
    }

    @Test
    public void execute() throws InterruptedException
    {
        String lock = "lock" + UUID.randomUUID().toString().intern();
        Response result = SdkExecutors.create().execute(HttpRequest.create()
                .file(new FileInfo("5-1.txt", "F:\\test_share_dir\\5\\5-1.txt")).contentType("application/json")
                .host("https://192.190.116.205:443").uri("/SIMP_DBS_S/event/file/analysis/upload/json")
                .method("POST").async().proxy(new ProxyHost("http://127.0.0.1", 8080)).build());
        System.out.println(result);
        Assert.assertTrue(true);
    }

    @Test
    public void executeGet() throws InterruptedException
    {
        Map<String, Object> param = new HashMap<>();
        param.put("current", "1");
        param.put("size", "10");
        param.put("startTime", "");
        param.put("endTime", "");
        Response result = SdkExecutors.create().execute(HttpRequest.create()
                .host("https://192.190.116.205:443").uri("/SIMP_DBS_S/system/taskCenter/getTaskCenterByPage").params(param).contentType("application/json")
                .method("GET").async().proxy(new ProxyHost("http://127.0.0.1", 8080)).build());
        System.out.println(result);
        Assert.assertTrue(true);
    }
    @Test
    public void executeGet1() throws InterruptedException
    {
        Map<String, Object> param = new HashMap<>();
        Response result = SdkExecutors.create().execute(HttpRequest.create()
                .host("https://192.190.116.205:443").uri("/SIMP_DBS_S/event/file/analysis/info/list?id=b8e52cb38e0a4c299163ca722b0764c7").params(param).contentType("application/json")
                .method("GET").async().build());
        System.out.println(result);
        Assert.assertTrue(true);
    }

    @Test
    public void executePost() throws InterruptedException
    {
        Map<String, Object> param = new HashMap<>();
        param.put("exportPolicy", "1f475b41570345cb9340cf84aec91498");
        param.put("exportPolicyName", "jianguoyun_15");
        param.put("name", UUID.randomUUID().toString().replace("-", ""));
        Response result = SdkExecutors.create().execute(HttpRequest.create()
                .host("https://192.190.116.205:443").uri("/SIMP_DBS_S/system/taskCenter/taskInfoExport").params(param).contentType("application/json")
                .method("POST").async().proxy(new ProxyHost("http://127.0.0.1", 8080)).build());
        System.out.println(result);
        Assert.assertTrue(true);
    }

    /**
     * 上传文件，回复文件检测结果
     *
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void upload() throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException
    {
        ExecutorService service = Executors.newFixedThreadPool(50);
        CommonInfo CommonInfo = PropertyLoader.loadProperties();
        AtomicInteger number = new AtomicInteger(0);
        IntStream.range(0, 20).forEach(i ->
                service.submit(() ->
                {
                    FileInfo fileInfo = new FileInfo("5-1.txt", "F:\\test_share_dir\\5\\5-1.txt");
                    Response result = SdkExecutors.create().init(CommonInfo)
                            .upload(HttpRequest.uploader()
                                    .file(fileInfo).build());
                    int in = number.incrementAndGet();
                    System.out.println(in);
                    if (result.getStatus() != 200)
                    {
                        System.out.println(result.getEventResult());
                        sleep();
                        return;
                    }
                    System.out.println(result.getEventResult().get("taskId"));
                    System.out.println(result.getEventResult().get("status"));
//                    System.out.println(result.getMessage().get("breachContent"));
                    System.out.println();
                    sleep();
                })
        );
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
    }

    private void sleep()
    {
        try
        {
            TimeUnit.SECONDS.sleep(15);
        }
        catch (InterruptedException e)
        {
        }
    }

    @Test
    public void uploadBigFile() throws InterruptedException, IOException
    {
        ExecutorService service = Executors.newFixedThreadPool(30);
        CommonInfo CommonInfo = PropertyLoader.loadProperties();
        AtomicInteger number = new AtomicInteger(0);
        AtomicInteger number2 = new AtomicInteger(0);
        System.setProperty("limit", "false");
        IntStream.range(0, 20).forEach(i ->
        {
            service.submit(() ->
            {
                FtpRequest ftpRequest = FtpRequest.upload()
                        .file(new FileInfo("1文档特征.zip", "F:\\Download\\DBS_S\\1文档特征.zip"))
                        .thenAnalyze()
                        .build();
                System.out.println(number2.incrementAndGet());
                Response ftp = SdkExecutors.create().init(CommonInfo).uploadBigFile(ftpRequest);
                int in = number.incrementAndGet();
                System.out.println(in);
                if (ftp.getStatus() != 200)
                {
                    System.out.println(ftp.getEventResult());
                    sleep();
                    return;
                }
                System.out.println(ftp.getEventResult().get("taskId"));
                System.out.println(ftp.getEventResult().get("status"));
//                System.out.println(ftp.getMessage().get("breachContent"));
                System.out.println();
                sleep();
            });
        });
        Assert.assertTrue(true);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
    }
}
