package com.yk.demo.dao;

import com.yk.demo.model.DemoModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DemoDAO
 */

@Repository
public class DemoDAO
{
    /**
     * mybatis
     */
    @Autowired(required = false)
    private SqlSessionTemplate sqlSessionTemplate;
    
    
    /**
     * spring-jdbc
     */
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    /**
     * redis
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 使用mybatis 查询数据库
     *
     * @param name
     * @return
     */
    public List<DemoModel> queryByName(String name)
    {
        return sqlSessionTemplate.selectList("demoMapper.demoQuery", name);
    }
    
    /**
     * 使用Spring-JDBC 查询数据库
     *
     * @param name
     * @return
     */
    public List<DemoModel> queryByName2(String name)
    {
        List<DemoModel> list = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM  t_demo WHERE name = ?", new Object[]{name}, resultSet ->
        {
            long id = resultSet.getLong(1);
            String nameValue = resultSet.getString(2);
            DemoModel demoModel = new DemoModel();
            demoModel.setId(id);
            demoModel.setName(nameValue);

            list.add(demoModel);
        });

        List<Map<String, Object>> result1 = jdbcTemplate.queryForList("SELECT * FROM  t_demo");

        List<Map<String, Object>> result = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM  t_demo", new Object[]{}, resultSet ->
        {
            long id = resultSet.getLong(1);
            String nameValue = resultSet.getString(2);
            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            m.put("name", nameValue);
            result.add(m);
        });
        return list;
    }
    
    /**
     * 查询redis索引
     *
     * @param name
     * @return
     */
    public Map<Object, Object> queryRedis(String name)
    {
        Map<Object, Object> value = redisTemplate.opsForHash().entries(name);
        return value;
    }
}
