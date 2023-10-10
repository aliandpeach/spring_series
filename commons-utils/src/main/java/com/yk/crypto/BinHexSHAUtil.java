package com.yk.crypto;

import java.math.BigInteger;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinHexSHAUtil
{
    /**
     * 数组拷贝 - 大于目标数组长度，根据目标数组的长度裁剪，小于目标数组长度， 从 dest.length - src.length 位置放入目标数组
     *
     * @param src  源
     * @param dest 目标
     */
    public static void to(byte[] src, byte[] dest)
    {
        if (src.length > dest.length)
        {
            System.arraycopy(src, src.length - dest.length, dest, 0, dest.length);
        }
        else if (src.length < dest.length)
        {
            System.arraycopy(src, 0, dest, dest.length - src.length, src.length);
        }
        else
        {
            System.arraycopy(src, 0, dest, 0, dest.length);
        }
    }
    
    /**
     * number   要转换的数
     * from     原数的进制
     * to       要转换成的进制
     */
    public static String change(String number, int from, int to)
    {
        return new BigInteger(number, from).toString(to);
    }
    
    /**
     * 将byte转为16进制 (1)
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes)
    {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (int i = 0; i < bytes.length; i++)
        {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1)
            {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    
    /**
     * 将byte转为16进制 (2)
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHex(byte[] bytes)
    {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1)
        {
            int char1 = ((bytes[index] >> 4) & 0xF);
            char chara1 = Character.forDigit(char1, 16);
            int char2 = ((bytes[index]) & 0xF);
            char chara2 = Character.forDigit(char2, 16);
            result.append(chara1);
            result.append(chara2);
        }
        return result.toString();
    }
    
    /**
     * 将byte转为16进制 (3)
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHex2(byte[] bytes)
    {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1)
        {
            
            String invalue1 = Integer.toHexString((bytes[index] >> 4) & 0xF);
            String intValue2 = Integer.toHexString(bytes[index] & 0xF);
            result.append(invalue1);
            result.append(intValue2);
        }
        return result.toString();
    }
    
    /**
     * 将16进制转为byte[]  (1)
     *
     * @param hex
     * @return
     */
    public static byte[] hexToByteArray(String hex)
    {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * 将16进制转为byte[]  (2)
     *
     * @param hexString
     * @return
     */
    public static byte[] hexToByteArray2(String hexString)
    {
        byte[] result = new byte[hexString.length() / 2];
        for (int len = hexString.length(), index = 0; index <= len - 1; index += 2)
        {
            String subString = hexString.substring(index, index + 2);
            int intValue = Integer.parseInt(subString, 16);
            result[index / 2] = (byte) intValue;
        }
        return result;
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

    public static byte[] binaryStringToBytes(String binaryString)
    {
        int length = binaryString.length() / 8;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
        {
            String binary = binaryString.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(binary, 2);
        }
        return bytes;
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

    public static String bytesToBinaryString(byte[] bytes)
    {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : bytes)
        {
            // 将每个字节转换为二进制字符串，并补足前导零
            String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binaryString.append(binary);
        }
        return binaryString.toString();
    }
    
    /**
     * 10进制数字转二进制字符串（不能用于转16进制和8进制）
     *
     * @param i     10进制数字
     * @param radix 进制只能是 2
     * @return 1010101010
     */
    public static String integer2BinaryString(long i, int radix)
    {
        Stack<Long> stack = new Stack<>();
        while (i > 0)
        {
            stack.push(i % radix);
            i = i / radix;
        }
        
        StringBuffer str = new StringBuffer();
        while (!stack.empty())
        {
            str.append(stack.pop());
        }
        
        return str.toString();
    }
    
    /**
     * 二进制字符串转10进制 （不能用于转16进制和8进制）
     *
     * @param s     101010101010
     * @param radix 进制只能是 2
     * @return int
     */
    public static long binaryString2Integer(String s, int radix)
    {
        long r = 0;
        char ary[] = s.toCharArray();
        Stack<Long> stack = new Stack<>();
        for (int k = ary.length - 1; k >= 0; k--)
        {
            stack.push(Long.parseLong(ary[k] + ""));
        }
        
        int t = stack.size() - 1;
        while (!stack.empty())
        {
            long p = stack.pop();
            r += p * (long) Math.pow(radix, t);
            t--;
        }
        return r;
    }

    /**
     * 获取字符串中所有的16进制子字符串，并将这些子字符串转换为char类型后再放入原来的位置
     */
    public static String replaceHexToChar()
    {
        String input = "Hello0x01|0x02$0x100x120x12World";
        Pattern _pattern = Pattern.compile("0x[0-9a-fA-F]{2}");
        Matcher _matcher = _pattern.matcher(input);
        StringBuffer output = new StringBuffer();
        while (_matcher.find())
        {
            String hexString = _matcher.group().substring(2); //去掉"0x"
            char c = (char) Integer.parseInt(hexString, 16);
            _matcher.appendReplacement(output, Character.toString(c));
        }
        _matcher.appendTail(output);
        return output.toString();
    }

    /**
     * 0x01类型的16进制字符串，删除0x开头
     */
    public static String hexToChar(String hex)
    {
        char c = (char) Integer.parseInt(hex, 16);
        return Character.toString(c);
    }

    /**
     * int转换为byte
     */
    public static byte intToByte(int i)
    {
        return (byte) (0xff & i);
    }
}