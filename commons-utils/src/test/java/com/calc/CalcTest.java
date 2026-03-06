package com.calc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class CalcTest
{
    @Test
    public void test()
    {
        int size = (int) Math.ceil(215267254 / 10000 / 4);
        int size2 = (int) Math.ceil(215267254 / 10000 / size);
        System.out.println(size);
        System.out.println(size2);

        System.out.println(Math.round(11.4));//11
        System.out.println(Math.round(11.5));//12
        System.out.println(Math.round(-11.4));//-11
        System.out.println(Math.round(-11.5));//-11

        System.out.println(Math.round(11.6));//12
        System.out.println(Math.round(-11.6));//-12

        System.out.println(Math.round(0.5));//1
        System.out.println(Math.round(-0.5));//0

        System.out.println();

        int totalRecord = 44001;
        int prefetch = 10000;
        int partitionNum = 4;
        test1(440000, 10000, 4);
        System.out.println();
        System.out.println();
        test2(440000, 10000, 4);
    }

    public void test1(int totalRecord, int prefetch, int partitionNum)
    {
        System.out.println("count: " + (totalRecord % prefetch == 0 ? totalRecord / prefetch : totalRecord / prefetch + 1));
        int partitionSize = ((int) Math.ceil((double) totalRecord / partitionNum / prefetch)) * prefetch;
        System.out.println("partitionSize: " + partitionSize);
        int readFinish = (int) Math.ceil((double) totalRecord / partitionSize);
        System.out.println("readFinish: " + readFinish);

        int co = 0;
        for (int i = 0; i < partitionNum; i++)
        {
            int beginRow = i * partitionSize;
            if (beginRow >= totalRecord)
            {
                break;
            }
            int endRow = (i + 1) * partitionSize;
            if (endRow > totalRecord)
            {
                endRow = totalRecord;
            }

            System.out.println("start: " + beginRow + ", end: " + endRow
                    + ", " + ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1)));
            co += ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1));
        }

        System.out.println(co);
    }

    public void test2(int totalRecord, int prefetch, int partitionNum)
    {
        // 分片每份大小
        double each = totalRecord / partitionNum;
        System.out.println("each: " + each);
        int eachActual = (each % prefetch > 0 ? ((int) each / prefetch) + 1 : (int) each / prefetch) * prefetch;
        System.out.println("eachActual: " + eachActual);
        int readFinish = totalRecord % eachActual > 0 ? (totalRecord / eachActual + 1) : totalRecord / eachActual;
        System.out.println("readFinish: " + readFinish);

        int co = 0;
        // 这里应该使用readFinish实际分片的数量, 而不是partitionNum, 现在用了而不是partitionNum因此才有break的逻辑判断
        // partitionNum 一定是大于readFinish, 因为是向上取整的
        for (int i = 0; i < partitionNum; i++)
        {
            int beginRow = i * eachActual;
            if (beginRow >= totalRecord)
            {
                break;
            }
            int endRow = (i + 1) * eachActual;
            if (endRow > totalRecord)
            {
                endRow = totalRecord;
            }

            System.out.println("start: " + beginRow + ", end: " + endRow
                    + ", " + ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1)));
            co += ((endRow - beginRow) % prefetch == 0 ? ((endRow - beginRow) / prefetch) : ((endRow - beginRow) / prefetch + 1));
        }

        System.out.println(co);
    }

    @Test
    public void calcNum() throws Exception
    {
        List<String> numbers = new ArrayList<>();
        for (int num = 0; num <= 999999; num++)
        {
            if (num >= 71371 && num < 200000)
                numbers.add("178" + String.format("%6d", num) + "58");
        }

        FileOutputStream outputStream = new FileOutputStream("D:\\numbers.txt");
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        HttpClientUtil clientUtil = new HttpClientUtil();
        String url = "https://cx.shouji.360.cn/phonearea.php?number=%s";

        for (String number : numbers)
        {
            String _url = String.format(url, number);
            String result = clientUtil.getString(_url, new HashMap<>(), new HashMap<>());
            JSONObject json1 = JSON.parseObject(result);
            if (json1 == null)
            {
                continue;
            }
            JSONObject data = json1.getJSONObject("data");
            if (data == null)
            {
                continue;
            }
            System.out.println(number + " " + data.get("province") + "  " + data.get("city") + "  " + data.get("sp"));
            Thread.sleep(1000 * new Random().nextInt(5 - 3 + 1) + 3);

            writer.write(number + " " + data.get("province") + "  " + data.get("city") + "  " + data.get("sp"));
            writer.write("\n");
            writer.flush();
        }

        writer.close();
        outputStream.close();
    }

    @Test
    public void read() throws Exception
    {
        String path = "D:\\";
        File dir = new File(path);
        if (!dir.isDirectory())
        {
            return;
        }
        File[] listFiles = dir.listFiles();
        if (listFiles == null)
        {
            return;
        }
        if (listFiles.length == 0)
        {
            return;
        }
        List<File> files = Arrays.stream(Optional.ofNullable(dir.listFiles(f -> null != f && f.isFile() && f.getName().startsWith("numbers_"))).orElse(new File[0]))
                .collect(Collectors.toList());

        List<String> allLines = new ArrayList<>();
        for (File f : files)
        {
            List<String> lines = FileUtil.readLines(f, StandardCharsets.UTF_8).stream().filter(l -> l.contains("陕西  西安")).collect(Collectors.toList());
            allLines.addAll(lines);
        }
        List<String> numbers = allLines.stream().map(l -> l.substring(0, l.length())).collect(Collectors.toList());
        System.out.println(numbers.size());
        FileUtil.writeLines(numbers, new File("D:\\numbers_sxxa.txt"), StandardCharsets.UTF_8);
    }
}
