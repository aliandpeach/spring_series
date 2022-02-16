package com.yk.demo.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Producer<T>
{
    public void print(T t)
    {
        System.out.print(t);
    }
}
