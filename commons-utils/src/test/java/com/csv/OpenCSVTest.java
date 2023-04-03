package com.csv;

import org.junit.Test;

import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2023/03/28 09:32:45
 */
public class OpenCSVTest
{
    @Test
    public void testSeperator()
    {
        String split = "|".chars().mapToObj(t -> "\\" + ((char) t)).collect(Collectors.joining(""));

        String[] row = "2011649420|2011649420|尾柿（裳亥）嚏浴优乒咳侣有限公司|702491748|".split(split, -1);
        System.out.println(row.length);

        String ch = "|" + ((char) Integer.parseInt("1d", 16)) + "|";
        String _split = ch.chars().mapToObj(t -> "\\" + ((char) t)).collect(Collectors.joining(""));
        row = ("2011649420" + ch + "2011649420" + ch + "尾柿（裳亥）嚏浴优乒咳侣有限公司" + ch + "702491748" + ch + "").split(_split, -1);
        System.out.println(row.length);
    }
}
