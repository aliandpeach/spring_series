package com.yk.base.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 16:09:38
 */
@Data
public class ResultEntity<T> implements Serializable
{
    private static final long serialVersionUID = 6493364710774892973L;

    private int code;

    private T data;

    public ResultEntity(int code, T t)
    {
        this.code = code;
        this.data = t;
    }

    public static ResultEntity<String> error(int statusCode)
    {
        return new ResultEntity<String>(statusCode, "ERROR");
    }
}
