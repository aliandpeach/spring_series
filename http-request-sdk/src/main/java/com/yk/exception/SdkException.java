package com.yk.exception;

/**
 * 异常
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/22 11:43
 */
public class SdkException extends RuntimeException
{
    private static final long serialVersionUID = 19794908406302677L;
    private int status;
    private Object data;

    public SdkException(String message)
    {
        super(message);
    }

    public SdkException(String message, int status)
    {
        super(message);
        this.status = status;
    }
    public SdkException(String message, int status, Object data)
    {
        super(message);
        this.status = status;
        this.data = data;
    }

    public SdkException(String message, Throwable e)
    {
        super(message, e);
    }

    public SdkException(String message, int status, Throwable e)
    {
        super(message, e);
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }

    public Object getData()
    {
        return data;
    }
}
