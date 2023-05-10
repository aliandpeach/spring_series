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
    private static final long serialVersionUID = -2865357400268162757L;
    /**
     * 错误码
     */
    protected int code;
    /**
     * 错误信息
     */
    protected String message;

    public GlobalException(String message)
    {
        super(message);
        this.message = message;
    }

    public GlobalException(String message, int code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }

    public GlobalException(String message, int code, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.code = code;
    }
}
