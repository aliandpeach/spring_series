package com.yk.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ApplicationTest
 */

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AppTest
{
    @Autowired
    private DemoService demoService;
    
    @Test
    public void test()
    {
        demoService.go();
    }
}
