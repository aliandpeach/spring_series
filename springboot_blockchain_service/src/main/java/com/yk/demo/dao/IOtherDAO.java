package com.yk.demo.dao;

import com.yk.demo.model.DemoModel;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DemoDAO
 */
@Mapper
public interface IOtherDAO
{
    List<DemoModel> queryBy(String name);
}
