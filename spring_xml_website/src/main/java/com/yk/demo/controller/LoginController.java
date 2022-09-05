package com.yk.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class LoginController
{
    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @Autowired
    private HttpServletRequest request;
    
    @RequestMapping("/login")
    public ModelAndView login()
    {
        return new ModelAndView("/mgr/login");
    }
    
    @RequestMapping("/password")
    public String password()
    {
        return "/mgr/password";
    }
    
    @RequestMapping("/upload")
    public String upload()
    {
        return "/mgr/upload";
    }
    
    @RequestMapping("/uploadFile")
    public void uploadFile(MultipartHttpServletRequest multipartFile, HttpServletRequest request)
    {
    }
}
