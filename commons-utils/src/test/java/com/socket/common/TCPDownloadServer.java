package com.socket.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/27 14:48:14
 */
public class TCPDownloadServer
{
    public static void main(String[] args)
    {
        File f = new File("test.zip");
        int port = 136;
        try (ServerSocket server_socket = new ServerSocket(port);
             Socket server = server_socket.accept();
             OutputStream fos = new FileOutputStream(f);
             InputStream is = server.getInputStream())
        {
            //创建指定端口号的服务器套接字ServerSocket
            //设置服务器套接字的等待时间，超过时间，自动关闭。以毫秒为单位
            server_socket.setSoTimeout(10000);
            int i;
            while ((i = is.read()) != -1)
            {
                fos.write(i);
            }
            fos.flush();
        }
        catch (IOException e)
        {
        }
        finally
        {
            System.out.println("接收完成");
        }
    }
}
