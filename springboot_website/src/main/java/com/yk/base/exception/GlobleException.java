package com.yk.base.exception;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 16:09:59
 */
public class GlobleException extends RuntimeException
{
    private static final long serialVersionUID = -67225319314290014L;

    private int status;

    private String message;

    public GlobleException(int status, String message)
    {
        super(message);
        this.status = status;
        this.message = message;
    }

    public GlobleException(int status, String message, Throwable e)
    {
        super(message, e);
        this.status = status;
        this.message = message;
    }

    public String getExMessage()
    {
        return message;
    }

    public int getStatus()
    {
        return status;
    }
}
