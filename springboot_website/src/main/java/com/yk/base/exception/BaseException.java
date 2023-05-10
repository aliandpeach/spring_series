package com.yk.base.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BaseException
 */
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BaseException extends RuntimeException
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

    public BaseException(String message)
    {
        super(message);
        this.message = message;
    }

    public BaseException(String message, int code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BaseException(String message, int code, Throwable e)
    {
        super(message, e);
        this.message = message;
        this.code = code;
    }

}
