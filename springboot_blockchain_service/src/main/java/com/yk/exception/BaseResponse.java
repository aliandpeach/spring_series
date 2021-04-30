package com.yk.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
public class BaseResponse<T>
{
    /**
     * 响应代码
     */
    private int status;
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * data
     */
    private T data;
    
    
    public BaseResponse()
    {
    }
    
    public BaseResponse(Integer status, String message, T data)
    {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Creates an ok result with message and data. (Default status is 200)
     *
     * @param data    result data
     * @param message result message
     * @return ok result with message and data
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String message, @Nullable T data)
    {
        return new BaseResponse<>(HttpStatus.OK.value(), message, data);
    }
    
    /**
     * Creates an ok result with message only. (Default status is 200)
     *
     * @param message result message
     * @return ok result with message only
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String message)
    {
        return ok(message, null);
    }
    
    /**
     * Creates an ok result with data only. (Default message is OK, status is 200)
     *
     * @param data data to response
     * @param <T>  data type
     * @return base response with data
     */
    public static <T> BaseResponse<T> ok(@Nullable T data)
    {
        return new BaseResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }
}
