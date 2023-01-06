package com.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

import static org.apache.commons.io.FileUtils.ONE_MB;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/11/03 10:58:33
 */
public class FileTest
{
    static long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException, InterruptedException
    {
        if (destFile.exists() && destFile.isDirectory())
        {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try
        {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size)
            {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        }
        finally
        {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fis);
        }

        Thread.sleep(3000);
        if (srcFile.length() != destFile.length())
        {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate)
        {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static void main(String[] args)
    {
        ExecutorService service = Executors.newFixedThreadPool(10);
        ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
        IntStream.range(0, 20).forEach(i ->
        {
            service.execute(() ->
            {
                try
                {
                    File dest = new File("C:\\Users\\Admin\\Desktop\\dest.txt");
                    FileUtils.deleteQuietly(dest);
                    lock.lock();
                    doCopyFile(new File("C:\\Users\\Admin\\Desktop\\src.txt"), dest, true);
                }
                catch (IOException | InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    lock.unlock();
                }
            });
        });
        service.shutdown();
    }
}
