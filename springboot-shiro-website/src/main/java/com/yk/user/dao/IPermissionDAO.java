package com.yk.user.dao;

import com.yk.user.model.Permission;
import com.yk.user.model.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IPermissionDAO
{
    List<Permission> queryPermissionListByRoleId(Map<String, String> param);
}
