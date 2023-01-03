package com.yk.login.controller;

import cn.hutool.core.map.MapUtil;
import com.yk.base.exception.BaseResponse;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.utils.RequestUtils;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
public class SignController
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> signin(@RequestBody User user)
    {
        User _user = userService.queryUserByUsername(user.getUsername());
        String _token = jwtTokenProvider.createToken(_user.getUsername(), _user.getRoleList());

        // 登录后redis写入token, 过期时间设定为token过期时间的两倍
        jwtTokenProvider.getRedisService().addValue(
                _user.getUsername().concat(":").concat(_token).concat(":").concat(RequestUtils.getBaseMetadata().getIp()),
                _token,
                3600 * 2,
                TimeUnit.SECONDS);
        return new BaseResponse<>(200, "", MapUtil.builder(new HashMap<String, String>()).put("Authorization", _token).build());
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> signup(@RequestBody User user)
    {
        return new BaseResponse<>(200, "", MapUtil.builder(new HashMap<String, String>()).build());
    }
}
