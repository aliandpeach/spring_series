package com.yk.base.exception;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/17 11:40:16
 */
public class CustomException extends RuntimeException
{
    private static final long serialVersionUID = -6263570294524298882L;

    private String message;

    private int code;

    public CustomException(String message)
    {
        super(message);
        this.message = message;
    }

    public CustomException(String message, int code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CustomException(String message, int code, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public int getCode()
    {
        return code;
    }
}
