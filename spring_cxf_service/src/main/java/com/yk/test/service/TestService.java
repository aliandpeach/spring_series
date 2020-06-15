package com.yk.test.service;

import com.yk.test.dao.TestDAO;
import com.yk.test.dao.TestInterfaceMybatisDAO;
import com.yk.test.datasource.Dynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestService
{
    @Autowired
    private TestDAO testDAO;

    @Autowired
    private TestInterfaceMybatisDAO testInterfaceMybatisDAO;

    @Dynamic
    public void testQuery(String id)
    {
        testDAO.testQuery();
    }

    @Dynamic
    public void testQueryTwo(String id)
    {
        testDAO.testQuery();
    }

    public List<Map<String, String>> testInterfaceMybatisQuery()
    {
        return testInterfaceMybatisDAO.testInterfaceMybatisQuery();
    }
}
