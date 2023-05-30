package com.yk.base.shiro.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@AllArgsConstructor
public class TokenForm implements AuthenticationToken
{
    private static final long serialVersionUID = -2432349047484188609L;

    public TokenForm(String username, String token)
    {
        this.username = username;
        this.token = token;
    }

    private String username;

    private String token;

    private String host;

    @Override
    public Object getPrincipal()
    {
        return username;
    }

    @Override
    public Object getCredentials()
    {
        return token;
    }
}
