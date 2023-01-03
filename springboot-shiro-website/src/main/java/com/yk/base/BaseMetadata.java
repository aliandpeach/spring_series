package com.yk.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMetadata implements Serializable
{
    private static final long serialVersionUID = 9171118218371230843L;

    private String ip;

    // ...
}
