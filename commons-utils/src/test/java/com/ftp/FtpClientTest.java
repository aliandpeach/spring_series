package com.ftp;

import org.junit.Test;

import java.io.IOException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/28 11:01:22
 */
public class FtpClientTest
{
    @Test
    public void test() throws IOException
    {
        FtpClient ftpClient = new FtpClient("192.168.20.252", 21, "yangkai", "Admin0123");
        ftpClient.download("data_1000w_3.txt", "D:\\data_1000w_3.txt", "/aaa");
    }
}
