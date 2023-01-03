package com.yk.base.shiro.realm;


import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.token.CustomerToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenRealm extends AuthorizingRealm
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection)
    {
        Object primaryPrincipal = principalCollection.getPrimaryPrincipal();
        return null;
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
}
