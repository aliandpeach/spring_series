package com.yk.user.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Role implements Serializable
{
    private static final long serialVersionUID = 2827241772715611027L;

    private String id;

    private String name;

    private List<Permission> permissionList = new ArrayList<>();
}
