package com.yk.base.exception;

import com.yk.httprequest.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Controller层异常处理
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/28 14:49:13
 */
@Component
public class AppHandlerExceptionResolver extends SimpleMappingExceptionResolver
{
    private static final Logger logger = LoggerFactory.getLogger(AppHandlerExceptionResolver.class);

    public AppHandlerExceptionResolver()
    {
        Properties properties = new Properties();
        properties.put("java.lang.Throwable", "error/500");
        super.setExceptionMappings(properties);
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ex.getStackTrace().length; i++)
        {
            sb.append(ex.getStackTrace()[i]).append("\r\n");
        }

        logger.error(sb.toString());

        if (handler instanceof HandlerMethod)
        {
            HandlerMethod method = (HandlerMethod) handler;
            // 如果是RestController, 则方法上没有ResponseBody注解, 则怎么办
            ResponseBody body = method.getMethodAnnotation(ResponseBody.class);
            if (body == null)
            {
                return super.doResolveException(request, response, handler, ex);
            }
            ModelAndView modelAndView = new ModelAndView();
            response.setStatus(HttpStatus.OK.value());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache,must-revalidate");

            try
            {
                Map<String, Object> ajaxMessage = buildErrorMessage(ex);
                String jsonObject = JSONUtil.toJson(ajaxMessage);
                response.getWriter().write(jsonObject);
            }
            catch (Exception e)
            {
                logger.error("convert to json error", e);
            }
            return modelAndView;
        }
        else
        {
            return super.doResolveException(request, response, handler, ex);
        }
    }

    private Map<String, Object> buildErrorMessage(Exception ex)
    {
        Map<String, Object> errors = new HashMap<String, Object>();
        if (ex instanceof ServiceException)
        {
            ServiceException serviceException = (ServiceException) ex;
            String errCode = serviceException.getErrorCode();
            if (StringUtils.isNotBlank(errCode))
            {
                errors.put("code", errCode);
                String message = messageSource.getMessage(errCode,
                        serviceException.getArgs(), Locale.getDefault());
                errors.put("message", message);
            }
            else
            {
                errors.put("message", ex.getMessage());
            }
        }
        else if (ex instanceof NullPointerException)
        {

            String errCode = "WEB.110001";
            errors.put("code", errCode);
            String errorMessage = messageSource.getMessage(errCode, null,
                    Locale.getDefault());
            errors.put("message", errorMessage);
        }
        else if (ex instanceof IOException)
        {

            String errCode = "WEB.110002";
            errors.put("code", errCode);
            String errorMessage = messageSource.getMessage(errCode, null, Locale.getDefault());
            errors.put("message", errorMessage);
        }
        else
        {

            String errCode = "WEB.110003";
            errors.put("code", errCode);
            String errorMessage = messageSource.getMessage(errCode, null, Locale.getDefault());
            errors.put("message", errorMessage);
        }

        return errors;
    }
}