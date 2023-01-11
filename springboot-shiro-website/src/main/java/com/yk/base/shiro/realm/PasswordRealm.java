package com.yk.base.shiro.realm;


import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.token.PasswordToken;
import com.yk.user.model.Permission;
import com.yk.user.model.Role;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordRealm extends AuthorizingRealm
{
    @Autowired
    private UserService userService;

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection)
    {
        return null;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken passwordAuth) throws AuthenticationException
    {
        if (!(passwordAuth instanceof PasswordToken))
        {
            throw new UnknownAccountException("帐号或密码为空");
        }
        if (null == passwordAuth.getPrincipal() || null == passwordAuth.getCredentials())
        {
            throw new UnknownAccountException("帐号或密码为空");
        }
        String username = (String) passwordAuth.getPrincipal();
        User user = userService.queryUserByUsername(username);
        if (null == user)
        {
            throw new ShiroException("未知帐号");
        }
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPasswd(), this.getName());
    }

    @Override
    public Class<?> getAuthenticationTokenClass()
    {
        return PasswordToken.class;
    }
}
