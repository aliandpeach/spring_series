package com.yk.demo.dao;

import com.yk.demo.model.DemoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * DemoDAO
 */
@Mapper
//@MapperScan("com.yk")
public interface IOtherDAO
{
    List<DemoModel> queryBy(String name);

    Map<String, Object> showVariable(String variableName);

    /**
     * 若不加@Param, 在mapper中获取值得用 #{param1}, #{param2} ...
     * mapper中不能写parameterType="java.lang.String", 否则异常, 因为两个参数不全是String类型
     */
    List<Map<String, Object>> query(@Param("id") int id, @Param("name") String name);
}
