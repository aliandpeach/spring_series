package com.yk.demo.model;

import lombok.Data;

import java.util.List;

/**
 * CommonParam
 */

@Data
public class CommonParam<T>
{
    private List<T> ids;
}
