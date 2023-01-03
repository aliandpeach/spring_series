package com.yk.base.shiro.matcher;

import com.yk.base.shiro.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenMatcher implements CredentialsMatcher
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token,
                                      AuthenticationInfo info)
    {
        boolean userEqual = token.getPrincipal().equals(info.getPrincipals().getPrimaryPrincipal());
        boolean validate = jwtTokenProvider.validateToken((String) token.getCredentials());
        return validate && userEqual;
    }
}
