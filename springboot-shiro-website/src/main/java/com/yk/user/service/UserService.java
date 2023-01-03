package com.yk.user.service;

import com.yk.user.dao.IUserDAO;
import com.yk.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService
{
    @Autowired
    private IUserDAO userDAO;

    public User queryUserByUserId(String id)
    {
        return userDAO.queryUserByUserId(id);
    }

    public User queryUserByUsername(String username)
    {
        return userDAO.queryUserByUsername(username);
    }

    public List<User> queryAllList()
    {
        return userDAO.queryAllList();
    }

    public List<User> queryAllList2()
    {
        return userDAO.queryAllList2();
    }
}
