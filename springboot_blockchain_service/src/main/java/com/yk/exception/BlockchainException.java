package com.yk.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * BlockchainException
 */
@Data
@AllArgsConstructor
public class BlockchainException extends RuntimeException
{
    private static final long serialVersionUID = 2698578246254095945L;
    /**
     * 业务错误码
     */
    protected int code;
    /**
     * 错误信息
     */
    protected String message;
    
    public BlockchainException()
    {
        super();
    }
    
}
