package com.fs;

import cn.hutool.core.util.HexUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FileUtilTest
{
    public static void main(String[] args) throws IOException
    {
        new FileUtilTest().testRandomAccessFileSingle();
    }

    /**
     * 测试 RandomAccessFile在多线程下是否线程安全 1
     */
    @Test
    public void testRandomAccessFileSingle() throws IOException
    {
        String fileString = "D:\\movies\\白鹿原1.mp4";
        String toFileString = "D:\\movies\\白鹿原1-1-1.mp4";
        long sliceSize = 2 * 1024 * 1024;
        long fileSize = new File(fileString).length();
        long chunks = fileSize % sliceSize == 0 ? fileSize / sliceSize : (fileSize / sliceSize + 1);

        Map<Integer, String> map1 = new HashMap<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(fileString), "rw"))
        {
            int len;
            byte[] buffer = new byte[(int) sliceSize];
            int index = 0;
            while ((len = randomAccessFile.read(buffer)) != -1)
            {
                byte[] b = new byte[len];
                System.arraycopy(buffer, 0, b, 0, len);
                String sha256 = DigestUtils.sha256Hex(b);
                System.out.print(sha256 + "  ");
                String hex = HexUtil.encodeHexStr(b);
                buffer = new byte[(int) sliceSize];

                map1.put(index++, hex);
            }
        }
        System.out.println(map1.keySet());
        System.out.println();

        Map<Integer, String> map2 = new HashMap<>();
        try (FileChannel channel = new RandomAccessFile(new File(fileString), "rw").getChannel())
        {
            int len;
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) sliceSize);
            int index = 0;
            while ((len = channel.read(byteBuffer)) != -1)
            {
                byteBuffer.limit(len);
                byteBuffer.flip();
                byte[] b = new byte[len];
                byteBuffer.get(b, 0, len);
                String sha256 = DigestUtils.sha256Hex(b);
                System.out.print(sha256 + "  ");

                byteBuffer.limit(len);
                byteBuffer.flip();
                b = new byte[len];
                byteBuffer.get(b, 0, len);
                String hex = HexUtil.encodeHexStr(b);

                byteBuffer.clear();

                map2.put(index++, hex);
            }
        }
        System.out.println(map2.keySet());

        System.out.println(map1.entrySet().stream().map(t -> t.getKey() + t.getValue()).collect(Collectors.joining(","))
                .equals(map2.entrySet().stream().map(t -> t.getKey() + t.getValue()).collect(Collectors.joining(","))));

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        System.out.println("toFileString delete : " + new File(toFileString).delete());

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(toFileString), "rw"))
        {
            for (Map.Entry<Integer, String> entry : new TreeMap<>(map1).entrySet())
            {

                futureList.add(CompletableFuture.runAsync(() ->
                {
                    byte[] bytes = HexUtil.decodeHex(entry.getValue());
                    int seek = entry.getKey();
                    try
                    {
                        synchronized (FileUtilTest.class)
                        {
                            randomAccessFile.seek(seek * sliceSize);
                            randomAccessFile.write(bytes);
                            randomAccessFile.getFD().sync();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }));
            }

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        }
    }

    /**
     * 测试 RandomAccessFile在多线程下是否线程安全 2
     */
    @Test
    public void testRandomAccessFileMultiple() throws Exception
    {
        String fileString = "D:\\movies\\白鹿原1.mp4";
        String toFileString = "D:\\movies\\白鹿原1-1-1.mp4";
        long sliceSize = 2 * 1024 * 1024;
        long fileSize = new File(fileString).length();
        long chunks = fileSize % sliceSize == 0 ? fileSize / sliceSize : (fileSize / sliceSize + 1);

        Map<Integer, String> map2 = new HashMap<>();
        try (FileChannel channel = new RandomAccessFile(new File(fileString), "rw").getChannel())
        {
            int len;
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) sliceSize);
            int index = 0;
            while ((len = channel.read(byteBuffer)) != -1)
            {
                byteBuffer.limit(len);
                byteBuffer.flip();
                byte[] b = new byte[len];
                byteBuffer.get(b, 0, len);
                String sha256 = DigestUtils.sha256Hex(b);
                System.out.print(sha256 + "  ");

                byteBuffer.limit(len);
                byteBuffer.flip();
                b = new byte[len];
                byteBuffer.get(b, 0, len);
                String hex = HexUtil.encodeHexStr(b);

                byteBuffer.clear();

                map2.put(index++, hex);
            }
        }
        System.out.println(map2.keySet());

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        System.out.println("toFileString delete : " + new File(toFileString).delete());

        for (Map.Entry<Integer, String> entry : map2.entrySet())
        {

            futureList.add(CompletableFuture.runAsync(() ->
            {
                byte[] bytes = HexUtil.decodeHex(entry.getValue());
                int seek = entry.getKey();
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(toFileString), "rw"))
                {
//                    synchronized (FileUtilTest.class)
//                    {
                    randomAccessFile.seek(seek * sliceSize);
                    randomAccessFile.write(bytes);
                    randomAccessFile.getFD().sync();
//                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }));
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }
}
