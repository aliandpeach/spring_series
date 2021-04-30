package com.yk.exception;

import lombok.Data;

/**
 * BlockchainException
 */
@Data
public class BlockchainException extends RuntimeException
{
    /**
     * 错误码
     */
    protected int status;
    /**
     * 错误信息
     */
    protected String message;
    
    public BlockchainException()
    {
        super();
    }
    
}
