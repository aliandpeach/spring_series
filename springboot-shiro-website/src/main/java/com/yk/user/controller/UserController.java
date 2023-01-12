package com.yk.user.controller;

import com.yk.base.exception.BaseResponse;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    @ResponseBody
    @RequiresRoles("ROLE_ADMIN")
    @RequiresPermissions("user:query")
    public BaseResponse<List<User>> queryAllList()
    {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        return new BaseResponse<>(200, "", userService.queryAllList());
    }
}
