package com.yk.user.dao;

import com.yk.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IUserDAO
{
    User queryUserByUserId(String id);

    User queryUserByUsername(String username);

    List<User> queryAllList();

    List<User> queryAllList2();
}
