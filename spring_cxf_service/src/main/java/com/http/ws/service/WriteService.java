package com.http.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/06 10:22:34
 */
@WebService(serviceName = "WriteService", targetNamespace = "WriteService.com")
public class WriteService
{
    @WebMethod(operationName = "writeSomething")
    @WebResult(name = "myReturn")
    public String sayHello(@WebParam(name = "name") String name)
    {
        return "hello: " + name;
    }

    public String sayGoodbye(String name)
    {
        return "goodbye: " + name;
    }

    @WebMethod(exclude = true)
    public String sayHello2(String name)
    {
        return "hello " + name;
    }
}
