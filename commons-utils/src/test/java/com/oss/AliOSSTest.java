package com.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AliOSSTest
{
    public AliOSSTest()
    {
    }


    public String create()
    {
        String tableName = "`web_url_" + index.incrementAndGet() + "`";
        String sql = "CREATE TABLE " + tableName + " (\n" +
                "  `path` varchar(2048),\n" +
                "  `url` varchar(2048),\n" +
                "  `size` bigint(20),\n" +
                "   KEY `index_url` (`url`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection2 = DriverManager.getConnection("jdbc:mysql://192.168.20.251:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
                 PreparedStatement statement = connection2.prepareStatement(sql))
            {
                statement.execute();
            }
            return tableName;
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void drop(String sql)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection2 = DriverManager.getConnection("jdbc:mysql://192.168.20.251:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
                 PreparedStatement statement = connection2.prepareStatement(sql))
            {
                statement.execute();
            }
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void insert(String line, Set<String> urls, String tableName)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        try (Connection connection2 = DriverManager.getConnection("jdbc:mysql://192.168.20.251:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
             PreparedStatement statement = connection2.prepareStatement("insert into " + tableName + " (`path`, `url`) values (?, ?)"))
        {
            for (String url : urls)
            {
                statement.setString(1, line);
                statement.setString(2, url);
                statement.addBatch();
            }
            statement.executeBatch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class Info
    {
        private String url;
        private String tableName;
        private long size;

        public Info(String url, String tableName)
        {
            this.url = url;
            this.tableName = tableName;
        }

        public long getSize()
        {
            return size;
        }

        public void setSize(long size)
        {
            this.size = size;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        public String getTableName()
        {
            return tableName;
        }

        public void setTableName(String tableName)
        {
            this.tableName = tableName;
        }
    }

    public static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(500000);
    public static final BlockingQueue<Info> queue2 = new LinkedBlockingQueue<>();

    public static final AtomicInteger index = new AtomicInteger();
    public static final AtomicBoolean finished = new AtomicBoolean();

    public static final Object lock = new Object();

    public static void main(String args[])
    {
        System.setProperty("log.home", "D:\\logs\\");
        AliOSSTest test = new AliOSSTest();
        new Thread(() ->
        {
            for (int i = 7; i <= 11; i++)
            {
                String tableName = "web_url_" + i;
                try
                {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                try (Connection connection2 = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
                     PreparedStatement statement = connection2.prepareStatement("select url from " + tableName + " where size = 0", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY))
                {
                    statement.setFetchSize(Integer.MIN_VALUE);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next())
                    {
                        String url = rs.getString("url");
                        if (null == url)
                        {
                            continue;
                        }
                        queue2.offer(new Info(url, tableName));
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            finished.set(true);
        }).start();


        List<Info> infoList = new ArrayList<>();
        Runnable run = () ->
        {
            while (true)
            {
                synchronized (lock)
                {
                    while (queue2.size() <= 0)
                    {
                        if (finished.get())
                        {
                            break;
                        }
                        try
                        {
                            lock.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
                Info _info = queue2.poll();
                if (null != _info)
                {
                    long _size = 0;
                    try
                    {
                        _size = test.size(_info.getUrl());
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }
                    _info.setSize(_size);
                    infoList.add(_info);
                }
                if (infoList.size() == 5000)
                {
                    System.out.println("1 infoList.size = " + infoList.size());
                    insertListBatch(infoList);
                    infoList.clear();
                }

                if (queue2.size() == 0 && infoList.size() < 5000 && finished.get() && infoList.size() > 0)
                {
                    System.out.println("2 infoList.size = " + infoList.size());
                    insertListBatch(infoList);
                    infoList.clear();
                    break;
                }

                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        };
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++)
        {
            service.execute(run);
        }
        service.shutdown();
    }

    private static void insertListBatch(List<Info> infoList)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        try (Connection connection2 = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
             Statement statement = connection2.createStatement())
        {
            for (Info e : infoList)
            {
                statement.addBatch("update " + e.getTableName() + " set size = " + e.getSize() + " where url = '" + e.getUrl() + "'");
            }
            statement.executeBatch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main3(String args[])
    {
        AliOSSTest test = new AliOSSTest();
        Set<String> linePathList = new HashSet<>();
        try (FileInputStream input = new FileInputStream("F:\\Downloads\\1BBCFED3-51FE-48a2-B3C6-7038FA369490(2).txt");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader rr = new BufferedReader(reader))
        {
            String line = null;
            while ((line = rr.readLine()) != null)
            {
                if (StringUtils.isEmpty(line) || StringUtils.isEmpty(line.trim()))
                {
                    continue;
                }
                linePathList.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(String.join("\n", linePathList));

        System.out.println("size = " + linePathList.size());
    }

    public static void main2(String args[])
    {
        AliOSSTest test = new AliOSSTest();
        Set<String> linePathList = new HashSet<>();
        try (FileInputStream input = new FileInputStream("F:\\Downloads\\1BBCFED3-51FE-48a2-B3C6-7038FA369490(2).txt");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             BufferedReader rr = new BufferedReader(reader))
        {
            String line = null;
            while ((line = rr.readLine()) != null)
            {
                if (StringUtils.isEmpty(line) || StringUtils.isEmpty(line.trim()))
                {
                    continue;
                }
                linePathList.add(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(String.join("\n", linePathList));

        System.out.println("size = " + linePathList.size());
        finished.set(false);

        new Thread(() ->
        {
            for (String line : linePathList)
            {
                List<String> urlList = test.test(line);
                for (String url : urlList)
                {
                    synchronized (lock)
                    {
                        while (queue.size() > 500000)
                        {
                            try
                            {
                                lock.wait();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    queue.offer(url);
                    synchronized (lock)
                    {
                        lock.notifyAll();
                    }
                }
            }
            finished.set(true);
        }).start();


        List<String> urls = new ArrayList<>();
        new Thread(() ->
        {
            while (true)
            {
                synchronized (lock)
                {
                    while (queue.size() <= 0)
                    {
                        if (finished.get())
                        {
                            break;
                        }
                        try
                        {
                            lock.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
                String url = queue.poll();
                if (null != url)
                    urls.add(url);
                if (urls.size() == 500000)
                {
                    System.out.println("index " + index.get() + " len = " + urls.size());
                    String tableName = test.create();
                    test.insert("", new HashSet<>(urls), tableName);
                    urls.clear();
                }

                if (queue.size() == 0 && urls.size() < 500000 && finished.get() && urls.size() > 0)
                {
                    System.out.println("finished len = " + urls.size());
                    String tableName = test.create();
                    test.insert("", new HashSet<>(urls), tableName);
                    urls.clear();
                    break;
                }

                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        }).start();
    }

    public List<String> test(String url)
    {
        ClientBuilderConfiguration ossConfig = new ClientBuilderConfiguration();
        ossConfig.setProxyPort(3128);
        ossConfig.setProxyHost("192.168.10.101");
        OSS ossClient = new OSSClientBuilder().build("oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn", "MfUWRW21GGvTFM2s", "rar5mQOEaDoM6WwEWgmbxAh3ZO08Y1", ossConfig);
        String presetPath = "oss://zjjcmspublic" + url.substring(url.indexOf("/jcms_files/"));
//        System.out.println(presetPath);
        String key = presetPath.endsWith("zjjcmspublic") ? "" : presetPath.substring(presetPath.indexOf("zjjcmspublic" + "/") + ("zjjcmspublic" + "/").length());
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setPrefix(key);
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setBucketName("zjjcmspublic");
        listObjectsRequest.setMaxKeys(1000);

        List<ObjectListing> more = new ArrayList<>();
        ObjectListing objectList = ossClient.listObjects(listObjectsRequest);
        String nextMarker = objectList.isTruncated() ? objectList.getNextMarker() : null;
        more.add(objectList);

        while (!StringUtils.isEmpty(nextMarker))
        {
            listObjectsRequest.setMarker(nextMarker);
            ObjectListing _objectList3 = ossClient.listObjects(listObjectsRequest);
            nextMarker = _objectList3.isTruncated() ? _objectList3.getNextMarker() : null;
            more.add(_objectList3);
        }

        AtomicLong atomicLong = new AtomicLong(0);

        List<String> allResult = new ArrayList<>();
        for (ObjectListing objectListing : more)
        {
            List<String> list = sub(objectListing, ossClient, "zjjcmspublic", atomicLong);
            allResult.addAll(list);
        }
//        HashSet<String> set = new HashSet<>(allResult);
//        System.out.println(allResult);
        return allResult;
    }

    public long size(String url)
    {
        ClientBuilderConfiguration ossConfig = new ClientBuilderConfiguration();
        ossConfig.setProxyPort(3128);
        ossConfig.setProxyHost("192.168.10.101");
        OSS ossClient = new OSSClientBuilder().build("oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn", "MfUWRW21GGvTFM2s", "rar5mQOEaDoM6WwEWgmbxAh3ZO08Y1", ossConfig);
        String presetPath = "oss://zjjcmspublic/" + url;
//        System.out.println(presetPath);
        String key = presetPath.endsWith("zjjcmspublic") ? "" : presetPath.substring(presetPath.indexOf("zjjcmspublic" + "/") + ("zjjcmspublic" + "/").length());
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata("zjjcmspublic", key);
        ossClient.shutdown();
        return objectMetadata.getContentLength();
    }

    private List<String> sub(ObjectListing objectList, OSS ossClient, String bucketName, AtomicLong atomicLong)
    {
        List<String> result = new ArrayList<>();
        for (OSSObjectSummary objectSummary : objectList.getObjectSummaries())
        {
            if (StringUtils.isEmpty(objectSummary.getKey()) || (objectSummary.getKey().endsWith("/") && objectSummary.getSize() == 0))
            {
                continue;
            }
            String objectSummaryKey = objectSummary.getKey();
            String objectSummaryBucketName = objectSummary.getBucketName();
            result.add("http://zjjcmspublic.oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn" + "/" + objectSummaryKey);
            atomicLong.addAndGet(objectSummary.getSize());
        }

        for (String commonPrefix : objectList.getCommonPrefixes())
        {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setPrefix(commonPrefix);
            listObjectsRequest.setDelimiter("/");
            listObjectsRequest.setBucketName(bucketName);
            listObjectsRequest.setMaxKeys(1000);
            ObjectListing _objectList = ossClient.listObjects(listObjectsRequest);
            List<ObjectListing> list = new ArrayList<>();
            list.add(_objectList);

            String nextMarker = _objectList.isTruncated() ? _objectList.getNextMarker() : null;

            while (!StringUtils.isEmpty(nextMarker))
            {
                listObjectsRequest.setMarker(nextMarker);
                ObjectListing _objectList2 = ossClient.listObjects(listObjectsRequest);
                nextMarker = _objectList2.isTruncated() ? _objectList2.getNextMarker() : null;
                list.add(_objectList2);
            }

            for (ObjectListing objectListing : list)
            {
                result.addAll(sub(objectListing, ossClient, bucketName, atomicLong));
            }

        }
        return result;
    }
}
