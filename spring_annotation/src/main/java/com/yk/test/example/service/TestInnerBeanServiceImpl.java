package com.yk.test.example.service;

import com.yk.test.example.dao.TestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("testInnerBeanService")
public class TestInnerBeanServiceImpl {

    @Autowired
    private TestDAO testDAO;

    public void test() {
        System.out.println("TestInnerBeanServiceImpl...");
        testDAO.testQuery();
    }
}
