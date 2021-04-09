package com.yk.demo;

import org.springframework.stereotype.Service;

/**
 * MissingService
 */
@Service("myService")
public class MyService
{
    public MyService()
    {
        System.out.println("...");
    }
    
    public void service()
    {
        System.out.println("myService");
    }
}
