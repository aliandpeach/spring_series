package com;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/10/27 10:03:29
 */
public class Main
{
    public static void main(String[] args)
    {
        DataService dataService1 = new DataService();
        DataService dataService2 = new DataService();
        boolean is = dataService1.get().equals(dataService2.get());
        boolean is2 = dataService1.get() == (dataService2.get());
        System.out.println();
    }
}
