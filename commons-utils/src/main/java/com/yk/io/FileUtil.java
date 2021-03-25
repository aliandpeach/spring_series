package com.yk.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
            
            long size = 102400;
            for (int i = 0; i < length; i++)
            {
                MappedByteBuffer mappedByteBuffer = in.map(FileChannel.MapMode.READ_WRITE, size * i, size);
                
                if (l - size * i < 102400)
                {
                    size = l - size * i;
                }
                else
                {
                    size = 102400;
                }
                byte[] buffer = new byte[(int) (size)];
                for (int j = 0; j < size; j++)
                {
                    buffer[j] = mappedByteBuffer.get(j);
                }
                
                MappedByteBuffer outtttttttttttt = out.map(FileChannel.MapMode.READ_WRITE, size * i, size);
                outtttttttttttt.put(buffer);
            }
        }
    }
}
