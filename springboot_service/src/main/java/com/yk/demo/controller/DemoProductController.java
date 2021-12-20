package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/demo")
public class DemoProductController
{

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ResponseBody
    public String index()
    {
        return "index demo";
    }

    /**
     * 使用@ResponseBody 用于处理非view的响应，所以produces不能设置为x-www-form-urlencoded (也不是完全不行，字符串可以被返回)
     * 不加@ResponseBody的情况是响应页面
     */
    @RequestMapping(method = RequestMethod.GET, value = "/get1", produces = "application/json")
    @ResponseBody
    public List<DemoModel> get1(@RequestParam Map<String, String> params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        return params.entrySet().stream().map(entry -> new DemoModel(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * RequestMethod.POST 参数为 @RequestParam， 请求体中需要传入的格式为 key1=value1&key2=value2
     */
    @RequestMapping(method = RequestMethod.POST, value = "/post1", produces = "application/json")
    @ResponseBody
    public List<DemoModel> post1(@RequestParam Map<String, String> params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        return params.entrySet().stream().map(entry -> new DemoModel(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post2", produces = "application/json")
    @ResponseBody
    public MultiValueMap<String, String> post2(@RequestBody MultiValueMap<String, String> params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        return params;
    }

    /**
     * 可不指定consumes, 客户端随意可指定Content-Type为 form-data x-www-form-urlencoded json text/plain application/xml等
     * springboot内部使用 MultiValueMap
     */
    @RequestMapping(method = RequestMethod.POST, value = "/post3", produces = "application/json")
    @ResponseBody
    public DemoModel post3(BindingAwareModelMap params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post4", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String post4(@RequestBody String params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        return params;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/post5", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public List<DemoModel> post5(@RequestBody Map<String, String> params)
    {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        return params.entrySet().stream().map(entry -> new DemoModel(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
