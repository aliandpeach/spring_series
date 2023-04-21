package com.yk.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DockerException
 */
@Data
@AllArgsConstructor
public class DockerException extends RuntimeException
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

    public DockerException(String message)
    {
        super(message);
        this.message = message;
    }

    public DockerException(String message, int code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }

    public DockerException(String message, int code, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.code = code;
    }

}
