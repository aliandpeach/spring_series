package com.yk.demo.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger("demo");

    /*@RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        return "index";
    }*/

    @RequestMapping("/index")
    public String test(ModelMap map) {
        map.put("thText", "设置文本内容");
        map.put("thUText", "设置文本内容");
        map.put("thValue", "设置当前元素的value值");
        map.put("thEach", Arrays.asList("列表", "遍历列表"));
        map.put("thIf", "msg is not null");
        map.put("thObject", new UserEntity("id", "username", "password", "description"));
        return "index";
    }

    @Data
    private class UserEntity {
        private String id;
        private String username;
        private String password;
        private String description;

        public UserEntity(String id, String username, String password, String description) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.description = description;
        }
    }
}