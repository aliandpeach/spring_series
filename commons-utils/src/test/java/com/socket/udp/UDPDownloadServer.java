package com.socket.udp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/27 14:48:14
 */
public class UDPDownloadServer
{
    private static ExecutorService service = Executors.newFixedThreadPool(10);

    private static final String SAVE_FILE_PATH = "E:/2013.mkv";

    public static void main(String[] args)
    {

        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];

        DatagramPacket dpk = null;
        DatagramSocket dsk = null;
        BufferedOutputStream bos = null;
        try
        {

            dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT));
            dsk = new DatagramSocket(UDPUtils.PORT + 1, InetAddress.getByName("localhost"));
            bos = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
            System.out.println("wait client ....");
            dsk.receive(dpk);

            int readSize = 0;
            int readCount = 0;
            int flushSize = 0;
            while ((readSize = dpk.getLength()) != 0)
            {
                // validate client send exit flag
                if (UDPUtils.isEqualsByteArray(UDPUtils.exitData, buf, readSize))
                {
                    System.out.println("server exit ...");
                    // send exit flag
                    dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
                    dsk.send(dpk);
                    break;
                }

                bos.write(buf, 0, readSize);
                if (++flushSize % 1000 == 0)
                {
                    flushSize = 0;
                    bos.flush();
                }
                dpk.setData(UDPUtils.successData, 0, UDPUtils.successData.length);
                dsk.send(dpk);

                dpk.setData(buf, 0, buf.length);
                System.out.println("receive count of " + (++readCount) + " !");
                dsk.receive(dpk);
            }
            bos.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (bos != null)
                    bos.close();
                if (dsk != null)
                    dsk.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


    }
}
