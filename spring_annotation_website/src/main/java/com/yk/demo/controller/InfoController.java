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
import java.util.Map;

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
     * <p>
     * ajax如果不指定上传数据类型Content-Type，默认的是application/x-www-form-urlencoded，这种编码格式后台需要通过RequestParam来处理。
     * <p>
     * 后台为什么参数名称是ids[] 因为前端传值，就是ids[]
     * <p>
     * https://blog.csdn.net/u013737132/article/details/106325903/
     * <p>
     *        $.ajax({
     *             type: "get",
     *             contentType: "application/x-www-form-urlencoded",
     *             url: "/rest/array",
     *             data: 'ids[]=' + 1 + '&ids[]=' + 2 + '&token=t',
     *             async: true,
     *             success: function (result) {
     *             }
     *         });
     */
    @RequestMapping("/array")
    @ResponseBody
    public CommonResult array(@RequestParam(value = "ids[]") List<Long> ids)
    {
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("key1", ids)));
    }

    /**
     *         $.ajax({
     *             type: "get",
     *             contentType: "application/x-www-form-urlencoded",
     *             url: "/rest/string",
     *             data: 'str=' + 11111 + '&token=t',
     *             async: true,
     *             success: function (result) {
     *                 var _range = result.range;
     *                 $("#_calc_result").html(_range);
     *             }
     *         });
     */
    @RequestMapping("/string")
    @ResponseBody
    public CommonResult string(@RequestParam(value = "str") String str)
    {
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("str", str)));
    }

    /**
     * 目前 @RequestParam(value = "params") 不能这么写，暂不知怎么把前端传来的 params 字符串直接通过Spring转成Map
     *
     *         $.ajax({
     *             type: "get",
     *             contentType: "application/x-www-form-urlencoded",
     *             url: "/rest/map",
     *             data: 'key1=value1&key2=value2&token=t',
     *             async: true,
     *             success: function (result) {
     *                 var _range = result.range;
     *                 $("#_calc_result").html(_range);
     *             }
     *         });
     */
    @RequestMapping("/map")
    @ResponseBody
    public CommonResult map(@RequestParam Map<String, String> params)
    {
        return CommonResult.buildSuccess(new HashMap<>(params));
    }
}