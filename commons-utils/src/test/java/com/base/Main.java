package com.base;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/09 14:18:39
 */
public class Main
{
    public static void main(String[] args)
    {
        InfoBase infoBase1 = new InfoBase();
        infoBase1.insert(new Info("1", "1", 1));
        InfoBase infoBase2 = new InfoBase();
        infoBase2.insert(new Info("2", "2", 1));
        System.out.println();
    }
}