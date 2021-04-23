package com.yk.bitcoin;

import com.yk.demo.BlockchainController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * BlockchainTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class BlockchainTest
{
    @Autowired
    private BlockchainController blockchainController;
    
    @Test
    public void test()
    {
        Map<String, String> result = blockchainController.brain(null);
        System.out.println(result);
    }
}
