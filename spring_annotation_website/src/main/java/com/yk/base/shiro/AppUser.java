package com.yk.base.shiro;

import lombok.Data;

@Data

public class AppUser
{
    private String username;

    private char[] passwd;

    public AppUser(String username, char[] passwd)
    {
        this.username = username;
        this.passwd = passwd;
    }
}
