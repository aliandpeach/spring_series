package com.yk.test.example.dao;

import com.yk.test.config.EnvConfig;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TestDAO {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private EnvConfig envConfig;

    public List<Map<String, Object>> testQuery() {
        System.out.println("test Query...");
        List<Map<String, Object>> list = sqlSessionTemplate.selectList("testDAO.testQuery");
        System.out.println(list);
        return list;
    }
}
