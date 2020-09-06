package com.yk.test.example.service;

import com.yk.test.example.dao.TestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("testInnerBeanService")
public class TestInnerBeanServiceImpl {

    @Autowired
    private TestDAO testDAO;

    public List<Map<String, Object>> test(Integer key, String val) {
        System.out.println("TestInnerBeanServiceImpl...");
        List<Map<String, Object>> r = testDAO.testQuery();
        return r;
    }
}
