package com.yk.bitcoin;

import org.springframework.stereotype.Service;

@Service
public class Cache
{
    public volatile boolean run = false;
}
