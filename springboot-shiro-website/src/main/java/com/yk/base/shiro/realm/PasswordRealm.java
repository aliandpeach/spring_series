package com.yk.base.shiro.realm;


import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.token.PasswordToken;
import com.yk.user.model.Permission;
import com.yk.user.model.Role;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
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
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
        String username = (String) SecurityUtils.getSubject().getSession().getAttribute("username");
        if (username == null)
        {
            return null;
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = userService.queryUserByUsername(username);
        if (user.getRoleList() == null)
        {
            return null;
        }
        for (Role role : user.getRoleList())
        {
            authorizationInfo.addRole(role.getName());
            for (Permission permission : role.getPermissionList())
            {
                authorizationInfo.addStringPermission(permission.getName());
            }
        }
        return authorizationInfo;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken passwordAuth) throws AuthenticationException
    {
        if (!(passwordAuth instanceof PasswordToken))
        {
            return null;
        }
        String username = (String) passwordAuth.getPrincipal();
        User user = userService.queryUserByUsername(username);
        if (null == user)
        {
            throw new ShiroException(400, "user not exist");
        }
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPasswd(), this.getName());
    }

    @Override
    public Class<?> getAuthenticationTokenClass()
    {
        return PasswordToken.class;
    }
}
