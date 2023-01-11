package com.yk.login.controller;

import cn.hutool.core.map.MapUtil;
import com.yk.base.exception.BaseResponse;
import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.redis.RedisServiceImpl;
import com.yk.base.utils.RequestUtils;
import com.yk.user.form.UserForm;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
public class SignController
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private SecurityManager securityManager;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> signin(@RequestBody @Validated UserForm user)
    {
        User _user = userService.queryUserByUsername(user.getUsername());
        String _token = jwtTokenProvider.createToken(_user.getUsername(), _user.getRoleList());

        // 登录后redis写入token, 过期时间设定为token过期时间的两倍
        String _key = _user.getUsername().concat(":").concat(_token).concat(":").concat(RequestUtils.getBaseMetadata().getIp());
        redisService.addValue(
                _key,
                _token,
                jwtTokenProvider.getValidityInMilliseconds() / 1000 * 20,
                TimeUnit.SECONDS);
        return new BaseResponse<>(200, "", MapUtil.builder(new HashMap<String, String>()).put("Authorization", _token).build());
    }

    @RequestMapping(value = "/singout", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> singout()
    {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated())
        {
            throw new ShiroException("已经退出");
        }
        securityManager.logout(subject);
        // delete redis key
        return new BaseResponse<>(200, "", null);
    }

    @RequestMapping(value = "/refresh/authorization", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> refreshAuthorizationInfo()
    {
        return new BaseResponse<>(200, "", null);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> signup(@RequestBody User user)
    {
        return new BaseResponse<>(200, "", MapUtil.builder(new HashMap<String, String>()).build());
    }
}
