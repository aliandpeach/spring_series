package com.yk.base.response;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 16:09:38
 */
public class ResultEntity<T> implements Serializable
{
    private static final long serialVersionUID = 6493364710774892973L;

    private int status;

    private T data;

    public ResultEntity(int statusCode, T t)
    {
        this.status = statusCode;
        this.data = t;
    }

    public static ResultEntity<String> error(int statusCode)
    {
        return new ResultEntity<String>(statusCode, "ERROR");
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
