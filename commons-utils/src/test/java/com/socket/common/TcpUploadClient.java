package com.socket.common;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/27 14:45:50
 */
public class TcpUploadClient
{

    public static void main(String[] args)
    {
        String file = "test.zip";
        //要连接的服务器IP地址
        String serverIp = "192.168.1.103";
        //要连接的服务器上的端口号，不是本地端口号
        int port = 136;
        try (Socket socket = new Socket(serverIp, port);
             OutputStream os = socket.getOutputStream();)
        {
            //创建指定IP地址和要连接对方的端口号的socket
            //scoket提供的获得对方地址和端口的方法
            System.out.println("连接" + socket.getRemoteSocketAddress());
            //用socket对象提供的方法获得输出的网络流
            FileInputStream fis = new FileInputStream(file);

            int i;
            System.out.print("传输中");
            while ((i = fis.read()) != -1)
            {
                os.write(i);
            }
            System.out.println();
            System.out.println("传输完成");
        }
        catch (Exception e)
        {
        }
        finally
        {
            System.out.println("连接断开");
        }
    }
}
