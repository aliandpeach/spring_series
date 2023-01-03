package com.yk.user.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Permission implements Serializable
{
    private static final long serialVersionUID = -4823097661150097191L;

    private String id;

    private String name;
}
