package com.yk.base.shiro;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordRealm extends AuthorizingRealm
{
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
        if (!(passwordAuth instanceof UsernamePasswordToken))
        {
            throw new UnknownAccountException("帐号或密码为空");
        }
        if (null == passwordAuth.getPrincipal() || null == passwordAuth.getCredentials())
        {
            throw new UnknownAccountException("帐号或密码为空");
        }
        // 根据username查询用户信息
        String username = (String) passwordAuth.getPrincipal();
        // 查到的用户信息(模拟)
        String _admin = "admin";
        char[] _passwd = new char[]{'A', 'd', 'm', 'i', 'n', '@', '1', '2', '3'};
        String encryptString = BCrypt.hashpw(String.valueOf(_passwd), BCrypt.gensalt());

        return new SimpleAuthenticationInfo(new AppUser(_admin, encryptString.toCharArray()), encryptString, this.getName());
    }

    @Override
    public Class<?> getAuthenticationTokenClass()
    {
        return UsernamePasswordToken.class;
    }
}
