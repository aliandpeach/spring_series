package com.yk.demo.model;

import lombok.Data;

/**
 * CommonResult
 */
@Data
public class CommonResult<T>
{
    private boolean success;
    
    private Integer errCode;
    
    private String errMessage;
    
    private T data;
    
    /**
     * Of response.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the response
     */
    public static <T> CommonResult<T> of(T data)
    {
        return buildSuccess(data);
    }
    
    /**
     * Build success CommonResult.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the CommonResult
     */
    public static <T> CommonResult<T> buildSuccess(T data)
    {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setSuccess(true);
        commonResult.setData(data);
        return commonResult;
    }
    
    /**
     * Build fail CommonResult.
     *
     * @param errCode    the err code
     * @param errMessage the err message
     * @return the CommonResult
     */
    public static CommonResult<?> buildFail(Integer errCode, String errMessage)
    {
        CommonResult<?> CommonResult = new CommonResult<>();
        CommonResult.setSuccess(false);
        CommonResult.setErrCode(errCode);
        CommonResult.setErrMessage(errMessage);
        return CommonResult;
    }
}
