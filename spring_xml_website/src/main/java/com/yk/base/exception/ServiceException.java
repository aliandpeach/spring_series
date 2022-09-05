package com.yk.base.exception;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/28 14:53:15
 */
public class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 4540003928853191177L;

    private String code;

    private Object[] args;

    public ServiceException()
    {
        super();
    }

    public ServiceException(String message)
    {
        super(message);
    }

    public ServiceException(String errorCode, Object... args)
    {
        this.code = errorCode;
        this.args = args;
    }

    public ServiceException(Throwable cause)
    {
        super(cause);
    }

    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceException(String errorCode, Throwable cause, Object... args)
    {
        this.code = errorCode;
        this.args = args;
    }

    public String getErrorCode()
    {
        return code;
    }

    public void setErrorCode(String errorCode)
    {
        this.code = errorCode;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public void setArgs(Object[] args)
    {
        this.args = args;
    }

}