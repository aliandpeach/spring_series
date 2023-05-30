package com.yk.base.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordMatcher extends HashedCredentialsMatcher
{
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
    {
        Object principal = token.getPrincipal();
        PrincipalCollection principals = info.getPrincipals();

        String submitPass = token.getCredentials() instanceof char[] ? String.valueOf((char[]) token.getCredentials()) : String.valueOf(token.getCredentials());
        String storePass = info.getCredentials() instanceof char[] ? String.valueOf((char[]) info.getCredentials()) : String.valueOf(info.getCredentials());
        return BCrypt.checkpw(submitPass, storePass);
    }
}
