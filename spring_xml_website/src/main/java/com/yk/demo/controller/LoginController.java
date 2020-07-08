package com.yk.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger("demo");


    @PostConstruct
    public void init() throws IOException {
        logger.info("LoginController init...");
    }
}
