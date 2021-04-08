package com.yk.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FileUtil
 */

public class FileUtil
{
    /**
     * 大文件的拷贝
     *
     * @throws IOException
     */
    public void copy(String src, String dest) throws IOException
    {
        File f = new File(src);
        try (FileChannel in = new RandomAccessFile(f, "rw").getChannel();
             FileChannel out = new RandomAccessFile(dest, "rw").getChannel())
        {
            long l = f.length();
            long length = l % 102400 == 0 ? (l / 102400) : (l / 102400) + 1;
    
            long pos = 102400;
            for (int i = 0; i < length; i++)
            {
                long bufferSize = pos;
                if (l - bufferSize * i < 102400)
                {
                    bufferSize = l - bufferSize * i;
                }
                MappedByteBuffer mappedByteBuffer = in.map(FileChannel.MapMode.READ_WRITE, pos * i, bufferSize);
                byte[] buffer = new byte[(int) (bufferSize)];
                for (int j = 0; j < bufferSize; j++)
                {
                    buffer[j] = mappedByteBuffer.get(j);
                }
                
                MappedByteBuffer outtttttttttttt = out.map(FileChannel.MapMode.READ_WRITE, pos * i, bufferSize);
                outtttttttttttt.put(buffer);
            }
        }
    }
    
    /**
     * 文件的拷贝
     *
     * @throws IOException
     */
    public void copyByChannel(String src, String dest) throws IOException
    {
        File f = new File(src);
        try (FileChannel in = new RandomAccessFile(f, "rw").getChannel();
             FileChannel out = new RandomAccessFile(dest, "rw").getChannel())
        {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }
    }
    
    /**
     * 文件的拷贝
     *
     * @throws IOException
     */
    public void copyCommon(String src, String dest) throws IOException
    {
        File f = new File(src);
        try (RandomAccessFile in = new RandomAccessFile(f, "rw");
             RandomAccessFile out = new RandomAccessFile(dest, "rw"))
        {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, len);
            }
        }
    }
}
