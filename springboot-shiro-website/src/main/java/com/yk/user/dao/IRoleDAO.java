package com.yk.user.dao;

import com.yk.user.model.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IRoleDAO
{
    List<Role> queryRoleListByUserId(Map<String, String> param);
}
