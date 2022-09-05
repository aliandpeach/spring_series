package com.yk.demo.controller;

import com.yk.demo.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/24 14:28:02
 */
@RestController
@RequestMapping("/db")
public class DbController
{
    @Autowired
    private DbService dbService;

    @RequestMapping("/query")
    public ResponseEntity<String> query()
    {

        dbService.doExec();
        return ResponseEntity.<String>ok("OK");
    }

    @RequestMapping("/query2")
    public ResponseEntity<String> query2()
    {

        dbService.doExecTemplate();
        return ResponseEntity.<String>ok("OK");
    }
}
