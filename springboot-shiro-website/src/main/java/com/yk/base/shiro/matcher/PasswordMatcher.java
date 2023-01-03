package com.yk.base.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatcher extends SimpleCredentialsMatcher
{
    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token,
                                      AuthenticationInfo info)
    {
        boolean passwdEqual = bcryptPasswordEncoder.matches((String) token.getCredentials(), (String) info.getCredentials());
        boolean userEqual = ((String) token.getPrincipal()).equals((String) info.getPrincipals().getPrimaryPrincipal());
        return userEqual && passwdEqual;
    }
}
