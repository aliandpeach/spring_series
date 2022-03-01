package com.yk.base.exception;

import com.yk.base.response.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 拦截异常
 *
 * @author qiurunze
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler
{

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResultEntity<String> exceptionHandler(HttpServletRequest request, Exception e)
    {
        if (e instanceof GlobalException)
        {
            GlobalException ex = (GlobalException) e;
            return ResultEntity.error(ex.getStatus());
        }
        else if (e instanceof BindException)
        {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            /**
             * 打印堆栈信息
             */
//            logger.error(String.format(msg, msg));
            return ResultEntity.error(400);
        }
        else
        {
            return ResultEntity.error(500);
        }
    }
}
