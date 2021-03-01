package com.yk.demo.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RoleQueryDAO extends SqlSessionDaoSupport
{
    /**
     * 构造函数注入
     *
     * @param sqlSessionFactory
     */
    @Autowired
    public RoleQueryDAO(SqlSessionFactory sqlSessionFactory)
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    public List<Map<String, Object>> queryRoles()
    {
        System.out.println("test Query...");
        List<Map<String, Object>> list = getSqlSessionTemplate().selectList("roleQueryRepository.queryRoles");
        System.out.println(list);
        return list;
    }
}
