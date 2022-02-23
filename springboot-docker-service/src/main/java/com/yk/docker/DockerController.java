package com.yk.docker;

import com.yk.base.exception.DockerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/02/21 12:03:48
 */
@RestController
@RequestMapping("/docker")
public class DockerController
{

    @RequestMapping("/query")
    @ResponseBody
    public ResponseEntity<String> query()
    {
        throw new DockerException("controller error", 403);
//        return ResponseEntity.ok("OK");
    }
}
