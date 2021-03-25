package com.yk.bitcoin;

import cn.hutool.core.util.HexUtil;
import org.junit.Test;

import java.math.BigInteger;

/**
 * GenKeyTest
 */

public class GenKeyTest
{
    @Test
    public void genKey()
    {
        StringBuilder max256BinaryString = new StringBuilder();
        for (int i = 0; i < 256; i++)
        {
            max256BinaryString.append("1");
        }
        byte[] max32bytes = binaryString2bytes(max256BinaryString.toString());
        // ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
        String maxHex = HexUtil.encodeHexStr(max32bytes);
        
        BigInteger zero = new BigInteger("0", 16);
        BigInteger one = new BigInteger("1", 16);
        BigInteger max = new BigInteger(maxHex, 16);
        for (BigInteger i = zero; i.compareTo(max) < 0; i = i.add(one))
        {
            byte[] barray = i.toByteArray();
            String hex = HexUtil.encodeHexStr(barray);
            byte[] key = new byte[32];
            byte[] f = barray;
            System.arraycopy(f, 0, key, key.length - f.length, f.length);
            KeyGenerator keyGenerator = new KeyGenerator();
            System.out.println(bytes2BinaryString(key));
            try
            {
                String prk = keyGenerator.keyGen(key, true);
                String puk = keyGenerator.addressGen(key);
                System.out.println("key= " + prk + ", address= " + puk);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 二制度字符串转字节数组
     *
     * @param input 输入二进制字符串。
     * @return 字节数组。
     */
    public static byte[] binaryString2bytes(String input)
    {
        StringBuilder in = new StringBuilder();
        
        // 不足 length % 8 位的前面补0  不能补到后面
        int remainder = input.length() % 8;
        if (remainder > 0)
        {
            for (int i = 0; i < 8 - remainder; i++)
            {
                in.append("0");
            }
        }
        in.append(input);
        
        byte[] bts = new byte[in.length() / 8];
        
        for (int i = 0; i < bts.length; i++)
        {
            bts[i] = (byte) Integer.parseInt(in.substring(i * 8, i * 8 + 8), 2);
        }
        
        return bts;
    }
    
    /**
     * 字节数组转二进制字符串
     *
     * @param bytes 转入字节数组。
     * @return 结果只包含 1 和 0 的二进制字符串。
     */
    public static String bytes2BinaryString(byte[] bytes)
    {
        String[] dic = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
        StringBuilder out = new StringBuilder();
        for (byte b : bytes)
        {
            String s = String.format("%x", b);
            s = s.length() == 1 ? "0" + s : s;
            out.append(dic[Integer.parseInt(s.substring(0, 1), 16)]);
            out.append(dic[Integer.parseInt(s.substring(1, 2), 16)]);
        }
        return out.toString();
    }
}
