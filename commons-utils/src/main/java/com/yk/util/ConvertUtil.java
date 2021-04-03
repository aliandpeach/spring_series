package com.yk.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertUtil
{
    /**
     * Integer[] -> List<Integer>
     *
     * @param integers integers
     * @return List<Integer>
     */
    public static List<Integer> asList(Integer[] integers)
    {
        return Arrays.asList(integers);
    }

    /**
     * ints[] -> List<Integer>
     *
     * @param ints ints
     * @return List<Integer>
     */
    public static List<Integer> asList(int[] ints)
    {
        return Arrays.stream(ints).boxed().collect(Collectors.toList());
    }

    /**
     * List<Integer> -> Integer[]
     *
     * @param list list
     * @return Integer[]
     */
    public static Integer[] toArrayBoxed(List<Integer> list)
    {
        return list.toArray(new Integer[0]);
    }

    /**
     * List<Integer> -> int[]
     *
     * @param list list
     * @return int[]
     */
    public static int[] toArrayUnboxed(List<Integer> list)
    {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Integer[] -> int[]
     *
     * @param integers integers
     * @return int[]
     */
    public static int[] unboxed(Integer[] integers)
    {
        return Arrays.stream(integers).mapToInt(Integer::intValue).toArray();
    }

    /**
     * int[] -> Integer[]
     *
     * @param ints ints
     * @return Integer[]
     */
    public static Integer[] boxed(int[] ints)
    {
        return Arrays.stream(ints).boxed().toArray(Integer[]::new);
    }


    public static char[] toCharArray(byte[] bytes, String charset)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length).put(bytes);
        char[] chars = Charset.forName(charset).decode(byteBuffer).array();
        return chars;
    }

    public static byte[] toByteArray(char[] chars, String charset)
    {
        CharBuffer charBuffer = CharBuffer.allocate(chars.length).put(chars);
        byte[] bytes = Charset.forName(charset).encode(charBuffer).array();
        return bytes;
    }

    /**
     * 判断字符串是否是16进制
     *
     * @param hex hex
     * @return boolean
     */
    public static boolean isHexString(String hex)
    {
        if (null == hex || hex.length() == 0)
        {
            return false;
        }
        char[] chars = hex.toCharArray();
        for (char ch : chars)
        {
            if (!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')))
            {
                return false;
            }
        }
        return true;
    }
}
