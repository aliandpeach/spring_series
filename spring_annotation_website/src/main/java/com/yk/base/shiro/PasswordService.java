package com.yk.base.shiro;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 作为PasswordMatcher的依赖
 */
@Deprecated
@Component
public class PasswordService extends DefaultPasswordService
{

    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String saved)
    {
        return true;
    }
}
