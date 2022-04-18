package com.yk.io;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static byte[] readLessThan(File file, long length)
    {
        try (InputStream input = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            long _length = Math.min(length, file.length());
            byte[] buf = new byte[8192];
            long size = _length % 8192 == 0 ? _length / 8192 : _length / 8192 + 1;
            int len;
            for (int i = 0; i < size; i++)
            {
                if (size - 1 == i)
                {
                    buf = new byte[Math.abs((int) (8192 * i - _length))];
                }
                len = input.read(buf);
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }

    public static byte[] readLessThan(byte[] file, long length)
    {
        try (InputStream input = new ByteArrayInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            long _length = Math.min(length, file.length);
            byte[] buf = new byte[8192];
            long size = _length % 8192 == 0 ? _length / 8192 : _length / 8192 + 1;
            int len;
            for (int i = 0; i < size; i++)
            {
                if (size - 1 == i)
                {
                    buf = new byte[Math.abs((int) (8192 * i - _length))];
                }
                len = input.read(buf);
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }

    public static byte[] readLessThan2(File file, int length)
    {
        try (InputStream input = new FileInputStream(file))
        {
            int _length = Math.min(length, (int) file.length());
            return IOUtils.toByteArray(input, _length);
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }

    public static byte[] readLessThan3(File file, int length)
    {
        try (InputStream input = new FileInputStream(file))
        {
            int _length = Math.min(length, (int) file.length());
            byte[] buf = new byte[_length];
            IOUtils.read(input, buf, 0, _length);
            return buf;
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }

    public static byte[] readLessThan(InputStream input, int length)
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            byte[] buf = new byte[8192];
            long size = length % 8192 == 0 ? length / 8192 : length / 8192 + 1;
            int len;
            for (int i = 0; i < size; i++)
            {
                if (size - 1 == i)
                {
                    buf = new byte[Math.abs((int) (8192 * i - length))];
                }
                len = input.read(buf);
                if (len == -1)
                {
                    break;
                }
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            return new byte[0];
        }
    }
}
