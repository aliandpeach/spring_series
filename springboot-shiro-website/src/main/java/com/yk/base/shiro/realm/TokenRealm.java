package com.yk.base.shiro.realm;


import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.token.CustomerToken;
import com.yk.user.model.Permission;
import com.yk.user.model.Role;
import com.yk.user.model.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.MapCache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenRealm extends AuthorizingRealm
{
    private static final String CACHE_AUTHORIZATION_INFO = "CACHE_AUTHORIZATION_INFO";

    private static final Logger logger = LoggerFactory.getLogger(TokenRealm.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection)
    {
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
//        String username = (String) SecurityUtils.getSubject().getSession().getAttribute("username");
        if (primaryPrincipal == null || StringUtils.isBlank(primaryPrincipal.toString()))
        {
            return null;
        }

        Cache<Object, AuthorizationInfo> authorizationCache = initAuthorizationInfoCache();

        AuthorizationInfo _authorizationInfo = authorizationCache.get(principalCollection);
        if (_authorizationInfo != null
                && !CollectionUtils.isEmpty(_authorizationInfo.getRoles())
                && !CollectionUtils.isEmpty(_authorizationInfo.getStringPermissions()))
        {
            return _authorizationInfo;
        }

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = jwtTokenProvider.getUserService().queryUserByUsername(primaryPrincipal.toString());
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
        // 写入缓存后, 之后权限判断时, 不再进入doGetAuthorizationInfo, 具体可查看 AuthorizingRealm.getAuthorizationInfo
        authorizationCache.put(principalCollection, authorizationInfo);
        return authorizationInfo;
    }

    private Cache<Object, AuthorizationInfo> initAuthorizationInfoCache()
    {
        Cache<Object, AuthorizationInfo> authorizationCache = this.getAuthorizationCache();
        if (null == authorizationCache)
        {
            synchronized (TokenRealm.class)
            {
                authorizationCache = this.getAuthorizationCache();
                if (null == authorizationCache)
                {
                    Map<Object, AuthorizationInfo> map = new ConcurrentHashMap<>();
                    this.setAuthorizationCache(new MapCache<>(CACHE_AUTHORIZATION_INFO, map));
                    authorizationCache = this.getAuthorizationCache();
                }
            }
        }
        return authorizationCache;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken customerToken) throws AuthenticationException
    {
        if (!(customerToken instanceof CustomerToken))
        {
            return null;
        }
        CustomerToken token = (CustomerToken) customerToken;
        String tokenCred = (String) token.getCredentials();
        return jwtTokenProvider.getJwtAuthenticationToken(tokenCred, this.getName());
    }

    @Override
    public Class<?> getAuthenticationTokenClass()
    {
        return CustomerToken.class;
    }

    /**
     * 重新赋值权限(在比如:给一个角色临时添加一个权限,需要调用此方法刷新权限,否则还是没有刚赋值的权限)
     */
    public void reloadAuthorizationInfo(String username)
    {
        Subject subject = SecurityUtils.getSubject();
        String realmName = this.getName();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(username, realmName);
        subject.runAs(principals);
        Cache<Object, AuthorizationInfo> cache = initAuthorizationInfoCache();
        cache.remove(subject.getPrincipals());

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = jwtTokenProvider.getUserService().queryUserByUsername(username);
        if (null == user.getRoleList())
        {
            logger.error("update user permission error, role list is empty {}", username);
        }
        for (Role role : user.getRoleList())
        {
            authorizationInfo.addRole(role.getName());
            for (Permission permission : role.getPermissionList())
            {
                authorizationInfo.addStringPermission(permission.getName());
            }
        }
        cache.put(subject.getPrincipals(), authorizationInfo);
    }
}
