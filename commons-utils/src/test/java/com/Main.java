package com;

import java.util.Base64;

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

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                DataService dataService1 = new DataService();
                dataService1.get();
            }
        }).start();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                DataService dataService1 = new DataService();
                dataService1.get();
            }
        }).start();
        DataService dataService2 = new DataService();
        dataService2.get();
        System.out.println();
    }
}
