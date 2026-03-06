package com.crypto;

import cn.hutool.core.util.HexUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class DmpTest
{
    public static void main(String[] args) throws Exception
    {
        RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\Spinfo\\Desktop\\TEST_FIVE_01.dmp", "rw");
        FileInputStream inputStream = new FileInputStream("C:\\Users\\Spinfo\\Desktop\\TEST_FIVE_01.dmp");
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Spinfo\\Desktop\\TEST_FIVE_OUT.dmp");

        // 拷贝: 一个一个字节的拷贝
        /*int b;
        while ((b = inputStream.read()) != -1)
        {
            outputStream.write(b);
        }*/


        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        char aa = 'A';
        byte bb = (byte) aa;
        String aaa = Integer.toHexString(bb);
        String hex1 = HexUtil.encodeHexStr(new byte[]{'I', 'N', 'S', 'E', 'R', 'T'});
        String hex2 = HexUtil.encodeHexStr(new byte[]{(byte) 'I', (byte) 'N', (byte) 'S', (byte) 'E', (byte) 'R', (byte) 'T'});
        String hex3 = HexUtil.encodeHexStr("INSERT".getBytes(StandardCharsets.UTF_8));

        int count = 0;
        int b;
        while ((b = inputStream.read()) != -1)
        {
            if ((byte) b == (byte) 0x0a)
            {
                System.out.println();
            }
            count++;
        }
    }
}
