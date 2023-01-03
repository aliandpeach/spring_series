package com.yk.test.user;

import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest
{
    @Autowired
    private UserService userService;

    @Before
    public void before()
    {
    }

    @Test
    public void testQueryAllList()
    {
        List<User> list = userService.queryAllList();
        System.out.println(list);
        List<User> list2 = userService.queryAllList2();
        System.out.println(list2);
    }
}
