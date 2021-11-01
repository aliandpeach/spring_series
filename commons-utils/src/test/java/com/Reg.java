package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/10/27 10:40:57
 */
public class Reg
{
    public static void main(String agrs[]) throws Exception
    {
        //String reg = "(?:(?:^.*\\*。123$))";
        String reg = "(测试)";
        try
        {
            Pattern pattern = Pattern.compile(reg);

            Matcher matcher = pattern.matcher("测试.123测试456");
            while (matcher.find())
            {
                System.out.println(matcher.group());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        System.out.println();

        String text = "测测试测试.123测试45测试6";
        String match = "测试";
        int max = Integer.MAX_VALUE;
        int fromIndex = 0;
//        System.out.println(text.indexOf(match, 7));
        while ((fromIndex = text.indexOf(match, fromIndex)) > -1)
        {
            System.out.println(fromIndex);
            fromIndex++;
        }
    }
}
