package com.yk.exception;

/**
 * BlockchainException
 */

public class BlockchainException extends RuntimeException
{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;
    
    public BlockchainException()
    {
        super();
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }
    
    public String getErrorMsg()
    {
        return errorMsg;
    }
    
    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }
}
