package com.socket.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/27 14:45:50
 */
public class TcpUploadClient
{
    private static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException
    {
        String serverIp = "localhost";
        int port = 1366;
        service.submit(new ChatClient(serverIp, port, new FileInputStream("C:\\Users\\yangkai\\Desktop\\Lucene 原理与代码分析完整版.pdf")));
    }

    private static class ChatClient implements Runnable
    {
        private String serverIp;

        private int serverPort;

        private InputStream input;

        public ChatClient(String serverIp, int serverPort, InputStream input)
        {
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.input = input;
        }

        @Override
        public void run()
        {
            try (Socket socket = new Socket(serverIp, serverPort);
                 OutputStream os = socket.getOutputStream())
            {
                int len;
                byte[] buf = new byte[8192];
                while ((len = input.read(buf)) != -1)
                {
                    os.write(buf, 0, len);
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    private static class ChatOnLineClient implements Runnable
    {
        private String serverIp;

        private int serverPort;

        public ChatOnLineClient(String serverIp, int serverPort)
        {
            this.serverIp = serverIp;
            this.serverPort = serverPort;
        }

        @Override
        public void run()
        {
            while (true)
            {
                try (Socket socket = new Socket(serverIp, serverPort);
                     OutputStream os = socket.getOutputStream())
                {
                    os.write("online".getBytes(StandardCharsets.UTF_8));
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
