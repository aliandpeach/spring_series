package com.yk.demo.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class DemoService implements InitializingBean
{
    public static Map<String, DemoCallback> callBackMap = new ConcurrentHashMap<>();
    
    public void send(String id, DemoCallback callback)
    {
        callBackMap.put(id, callback);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        new Thread(() ->
        {
            while (true)
            {
                int random = new Random().nextInt(6) + 5;
                try
                {
                    TimeUnit.SECONDS.sleep(random);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if (callBackMap.keySet().iterator().hasNext())
                {
                    String id = callBackMap.keySet().iterator().next();
                    callBackMap.get(id).onFinish(Collections.singletonMap(id, "finished"));
                    callBackMap.remove(id);
                }
            }
        }).start();
    }
}
