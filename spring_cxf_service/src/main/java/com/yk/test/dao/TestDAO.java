package com.yk.test.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TestDAO
{
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public void testQuery()
    {
        System.out.println("test Query...");
        List<Map<String, String>> list = sqlSessionTemplate.selectList("testDAO.testQuery");
        System.out.println(list);
    }
}
