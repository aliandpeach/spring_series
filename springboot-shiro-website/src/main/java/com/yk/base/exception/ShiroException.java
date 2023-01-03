package com.yk.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DockerException
 */
@Data
@AllArgsConstructor
public class ShiroException extends RuntimeException
{
    private static final long serialVersionUID = 3590874415552570389L;
    /**
     * 错误码
     */
    protected int status;
    /**
     * 错误信息
     */
    protected String message;

    public ShiroException(String message)
    {
        super(message);
        this.message = message;
    }

    public ShiroException(String message, int status)
    {
        super(message);
        this.message = message;
        this.status = status;
    }

    public ShiroException(String message, int status, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.status = status;
    }

}
