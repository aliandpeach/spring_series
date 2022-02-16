package com.yk.demo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrintModel implements Serializable
{
    private String name;

    private String message;

    private String code;
}
