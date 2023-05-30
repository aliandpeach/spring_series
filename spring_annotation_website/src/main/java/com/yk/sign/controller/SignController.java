package com.yk.sign.controller;

import com.yk.sign.model.AppUserForm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * SignController
 */
@RestController
@RequestMapping("/user")
public class SignController
{
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> signin(AppUserForm appUserForm)
    {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(appUserForm.getUsername(), appUserForm.getPasswd());
        Subject subject = SecurityUtils.getSubject();
        subject.login(usernamePasswordToken);
        boolean isAuthenticated = subject.isAuthenticated();
        Session session = subject.getSession(true);
        return ResponseEntity.ok(Collections.singletonMap("JSESSIONID", session.getId()));
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> error()
    {
        throw new RuntimeException("2345");
    }
}