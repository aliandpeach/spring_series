package com.yk.demo.controller;

import com.yk.demo.model.CommonParam;
import com.yk.demo.model.CommonResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * PageController
 */
@RestController
@RequestMapping("/rest")
public class InfoController
{
    /**
     * {"ids":["1", "2"]}
     *
     * @param body
     * @return
     */
    @RequestMapping("/of")
    @ResponseBody
    public CommonResult of(@RequestBody CommonParam<String> body)
    {
        List<String> list = body.getIds();
        
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("key1", "val1")));
    }
    
    /**
     * 后台为什么使用@RequestParam解析？
     *
     * ajax如果不指定上传数据类型Content-Type，默认的是application/x-www-form-urlencoded，这种编码格式后台需要通过RequestParam来处理。
     *
     * 后台为什么参数名称是ids[] 因为前端传值，就是ids[]
     *
     * https://blog.csdn.net/u013737132/article/details/106325903/
     *
     *
     */
    @RequestMapping("/de")
    @ResponseBody
    public CommonResult de(@RequestParam(value = "ids[]") List<Long> ids)
    {
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("key1", "val1")));
    }
}