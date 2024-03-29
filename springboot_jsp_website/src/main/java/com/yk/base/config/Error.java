package com.yk.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * BasicErrorController 默认会返回new ModelAndView("error/xxx") （页面需要放入view目录下）
 *
 * DefaultErrorViewResolver默认会返回 /error/4xx.jsp /error/5xx.jsp （页面就需要放入 views (BaseWebMvcConfiguration中配置) 目录下面的error目录）
 *
 * 用户实现 ErrorController 覆盖掉了 ErrorMvcAutoConfiguration 的 basicErrorController,
 * 如果没有自定义, 会去执行 BasicErrorController, 其内部通过注入的 DefaultErrorViewResolver 寻找有没有配置 /error/4xx.jsp /error/5xx.jsp页面, 没有就直接返回error.jsp
 *
 * Error -> BasicErrorController -> DefaultErrorViewResolver
 */
@Controller
public class Error implements ErrorController
{
    private static final String ERROR_PATH = "/error";
    
    private ErrorAttributes errorAttributes;
    
    @Override
    public String getErrorPath()
    {
        return ERROR_PATH;
    }
    
    @Autowired
    public Error(ErrorAttributes errorAttributes)
    
    {
        this.errorAttributes = errorAttributes;
    }
    
    /**
     * web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ModelAndView errorPageHandler(HttpServletRequest request, HttpServletResponse response)
    {
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE));
        return new ModelAndView("400"); // 不存在或者内部错误的资源请求进入 views/400 页面
    }
    
    /**
     * 除web页面外的错误处理，比如json/xml等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public Map<String, Integer> errorApiHandler(HttpServletRequest request)
    {
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE));
        return new HashMap<>(Collections.singletonMap("status", 400));
    }
}
