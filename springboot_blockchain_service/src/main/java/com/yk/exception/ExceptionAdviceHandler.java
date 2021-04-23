package com.yk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * ExceptionAdvice
 */

@ControllerAdvice
public class ExceptionAdviceHandler
{
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdviceHandler.class);
    
    /**
     * 处理自定义的业务异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BlockchainException.class)
    @ResponseBody
    public ResultBody bizExceptionHandler(HttpServletRequest req, BlockchainException e)
    {
        logger.error("发生业务异常！原因是：{}", e);
        return ResultBody.error(e.getErrorCode(), e.getErrorMsg());
    }
    
    /**
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, NullPointerException e)
    {
        logger.error("发生空指针异常！原因是:" + e.getMessage());
        return ResultBody.error("NullPointerException");
    }
    
    
    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest req, Exception e)
    {
        logger.error("未知异常！原因是:", e);
        return ResultBody.error("Exception");
    }
}
