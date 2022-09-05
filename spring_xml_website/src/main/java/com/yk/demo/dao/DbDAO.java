package com.yk.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/24 17:08:23
 */
@Mapper
public interface DbDAO /*extends SqlSessionDaoSupport*/
{
    List<Map<String, Object>> query();

    int insert(Map<String, Object> param);
}
