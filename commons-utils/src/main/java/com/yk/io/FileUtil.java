package com.yk.io;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * FileUtil
 */

public class FileUtil
{
    public static void copy(long start, InputStream input, String dest) throws IOException
    {
        MappedByteBuffer out = null;
        try (InputStream _input = input; FileChannel channel = new RandomAccessFile(dest, "rw").getChannel())
        {
            byte[] bytes = IOUtils.toByteArray(_input);
            out = channel.map(FileChannel.MapMode.READ_WRITE, start, bytes.length);
            out.put(bytes);
        }
        finally
        {
            freedMappedByteBuffer(out);
        }
    }

    public static void copyRandom(long start, InputStream input, String dest) throws IOException
    {
        try (InputStream _input = input; RandomAccessFile randomAccessFile = new RandomAccessFile(dest, "rw"))
        {
            randomAccessFile.seek(start);
            randomAccessFile.write(IOUtils.toByteArray(_input));
        }
    }

    /**
     * 大文件的拷贝
     */
    public static void copy(InputStream input, File dest) throws IOException
    {
        try (ReadableByteChannel channelInput = Channels.newChannel(input);
             RandomAccessFile out = new RandomAccessFile(dest, "rw"))
        {
            long length = input.available();

            final long size = 102400;
            long split = length % size == 0 ? (length / size) : (length / size + 1);
            for (int i = 0; i < split; i++)
            {
                long bufferSize = size;
                if (length - bufferSize * i < size)
                {
                    bufferSize = length - bufferSize * i;
                }

                ByteBuffer buffer = ByteBuffer.allocate((int) bufferSize);
                while (channelInput.read(buffer) != -1)
                {
                    buffer.flip();
                    out.seek(size * i);
                    out.write(buffer.array(), 0, (int) bufferSize);
                    buffer.clear();
                }
            }
        }
    }

    /**
     * 大文件的拷贝
     *
     * @throws IOException
     */
    public static void copy(String src, String dest) throws IOException
    {
        File f = new File(src);
        try (FileChannel channelInput = new RandomAccessFile(f, "rw").getChannel();
             FileChannel channelOut = new RandomAccessFile(dest, "rw").getChannel())
        {
            long length = f.length();

            final long size = 102400;
            long split = length % size == 0 ? (length / size) : (length / size + 1);
    
            for (int i = 0; i < split; i++)
            {
                long bufferSize = size;
                if (length - bufferSize * i < size)
                {
                    bufferSize = length - bufferSize * i;
                }
                MappedByteBuffer mappedByteBuffer = channelInput.map(FileChannel.MapMode.READ_WRITE, size * i, bufferSize);
                byte[] buffer = new byte[(int) (bufferSize)];
                for (int j = 0; j < bufferSize; j++)
                {
                    buffer[j] = mappedByteBuffer.get(j);
                }
                
                MappedByteBuffer out = channelOut.map(FileChannel.MapMode.READ_WRITE, size * i, bufferSize);
                out.put(buffer);
            }
        }
    }
    
    /**
     * 文件的拷贝
     *
     * @throws IOException
     */
    public static void copyByChannel(String src, String dest) throws IOException
    {
        File f = new File(src);
        try (FileChannel in = new RandomAccessFile(f, "rw").getChannel();
             FileChannel out = new RandomAccessFile(dest, "rw").getChannel())
        {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (in.read(buffer) != -1)
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
    public static void copyCommon(String src, String dest) throws IOException
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

    public static void freedMappedByteBuffer(final MappedByteBuffer mappedByteBuffer)
    {
        try
        {
            if (mappedByteBuffer == null)
            {
                return;
            }

            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction<Object>()
            {
                @Override
                public Object run()
                {

                    try
                    {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clean();
                    }
                    catch (Exception e)
                    {
                    }
                    return null;
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
