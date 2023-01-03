package com.yk.base.shiro.token;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

@Data
public class PasswordToken implements AuthenticationToken
{
    private static final long serialVersionUID = -7832063338283190310L;

    private String username;

    private String passwd;

    private String host;

    @Override
    public Object getPrincipal()
    {
        return username;
    }

    @Override
    public Object getCredentials()
    {
        return passwd;
    }
}
