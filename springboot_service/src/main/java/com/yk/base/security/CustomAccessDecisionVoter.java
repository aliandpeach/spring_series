package com.yk.base.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

/**
 * 未验证的自定义校验类
 */
public class CustomAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation>
{

    @Override
    public boolean supports(ConfigAttribute attribute)
    {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz)
    {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes)
    {
        String requestUrl = fi.getRequestUrl();
        for (GrantedAuthority authority : authentication.getAuthorities())
        {
            if (authority.getAuthority().equals(requestUrl))
            {
                return ACCESS_GRANTED;
            }
        }
        return ACCESS_DENIED;
    }
}

