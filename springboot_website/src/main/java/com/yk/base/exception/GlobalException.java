package com.yk.base.exception;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 16:09:59
 */
public class GlobalException extends RuntimeException
{
    /**
     * 错误码
     */
    protected int status;
    /**
     * 错误信息
     */
    protected String message;

    public GlobalException(String message)
    {
        super(message);
        this.message = message;
    }

    public GlobalException(String message, int status)
    {
        super(message);
        this.message = message;
        this.status = status;
    }

    public GlobalException(String message, int status, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.status = status;
    }
}
