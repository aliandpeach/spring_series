package com.yk.user.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class User implements Serializable
{
    private static final long serialVersionUID = -5198110295438372500L;

    private String id;

    private String username;

    private String passwd;

    private List<Role> roleList = new ArrayList<>();
}
