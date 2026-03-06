package com.yk.base.exception;

import lombok.Data;

@Data
public class ShiroException extends RuntimeException
{
    private static final long serialVersionUID = 3590874415552570389L;
    /**
     * 错误码
     */
    protected int code;
    /**
     * 错误信息
     */
    protected String message;

    public ShiroException(String message, int code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }

    public ShiroException(String message, int code, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.code = code;
    }

}
