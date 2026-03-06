package com.crypto;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.CharUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2023/07/18 11:59:58
 */
public class CsvTest
{
    @Test
    public void test() throws IOException
    {
        FileInputStream input = new FileInputStream("D:\\workspace\\SDM_branches\\datamask\\datamask-simp-sdm2021-optimization\\datamask\\100W.csv");
        InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line4 = bufferedReader.readLine();

        String split = "\",\"";
        String[] args = Arrays.stream(line4.split(split)).map(t -> t.replace("\"", "")).toArray(String[]::new);
        if (args[0].startsWith("\uFEFF"))
        {
            System.out.println();
        }
        byte[] _bytes = args[0].getBytes(StandardCharsets.UTF_8);
        byte[] _bytes3 = args[1].getBytes(StandardCharsets.UTF_8);
        byte[] _bytes2 = "34248272".getBytes(StandardCharsets.UTF_8);

        int i3 = 0xEF;
        char i = (char) Integer.parseInt("EF", 16);
        System.out.println(Character.toString(i));

        byte[] bbb1 = UnicodeUtil.toString("\\uFEFF").getBytes(StandardCharsets.UTF_8);
        byte[] bbb2 = (new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        byte[] bbb5 = (new byte[]{-17, (byte) 0xBB, (byte) 0xBF});
        byte[] bbb3 = new String(bbb2, 0, bbb2.length).getBytes(StandardCharsets.UTF_8);

        System.out.println(Integer.toBinaryString(0xef));
        System.out.println(Integer.toBinaryString((byte) 0xEF));

        String uin = UnicodeUtil.toUnicode(new String(bbb2, 0, bbb2.length), false);

        for (byte by : bbb1)
        {
            String a = Integer.toHexString(by & 0xff);
            System.out.println(a);
        }

        String hex = Integer.toHexString('a');
        System.out.println(hex);
        hex = Integer.toHexString('f');
        System.out.println(hex);
        System.out.println(UnicodeUtil.toString("\\u6768"));
        System.out.println(UnicodeUtil.toUnicode("杨", false));

        System.out.println((int) Integer.parseInt("6768", 16));
        System.out.println((char) Integer.parseInt("6768", 16));
        boolean is = CharUtil.isAscii((char) Integer.parseInt("6768", 16));
        System.out.println(is);
    }
}

