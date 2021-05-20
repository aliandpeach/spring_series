package com.yk.demo.dao;

import com.yk.demo.model.DemoModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * DemoDAO
 */
@Mapper
//@MapperScan("com.yk")
public interface IOtherDAO
{
    List<DemoModel> queryBy(String name);
}
