package com.socket.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/27 14:48:14
 */
public class TCPDownloadServer
{
    private static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException
    {
        int port = 1366;
        InetAddress inetAddress = InetAddress.getByName("localhost");
        //创建指定端口号的服务器套接字ServerSocket
        ServerSocket server_socket = new ServerSocket(port, 0, inetAddress);
        //设置服务器套接字的等待时间，超过时间，自动关闭。以毫秒为单位
//        server_socket.setSoTimeout(10000);
        while (true)
        {
            Socket socket = server_socket.accept();
            service.submit(new ChatSocket(socket));
        }
    }

    private static class ChatSocket implements Runnable
    {
        private Socket socket;

        public ChatSocket(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            File f = new File("C:\\Users\\yangkai\\Desktop\\11" + System.currentTimeMillis() + ".xml");
            try (InputStream in = socket.getInputStream();
                 OutputStream fos = new FileOutputStream(f))
            {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1)
                {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            }
            catch (IOException e)
            {
            }
        }
    }
}
