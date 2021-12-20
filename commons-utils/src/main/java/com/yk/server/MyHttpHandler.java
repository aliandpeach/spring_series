package com.yk.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/13 10:13:16
 */
public class MyHttpHandler implements HttpHandler
{
    @Override

    public void handle(HttpExchange httpExchange)
    {
        try
        {
            StringBuilder responseText = new StringBuilder();
            responseText.append("Method：").append(httpExchange.getRequestMethod()).append("\n");
            responseText.append("Parameters：").append(getRequestParam(httpExchange)).append("\n");
            responseText.append("Headers：").append(getRequestHeader(httpExchange));

            handleResponse(httpExchange, responseText.toString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 获取请求头
     */
    private String getRequestHeader(HttpExchange httpExchange)
    {
        Headers headers = httpExchange.getRequestHeaders();
        return headers.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().toString()).collect(Collectors.joining(";"));
    }

    /**
     * 获取请求参数
     */

    private String getRequestParam(HttpExchange httpExchange) throws Exception
    {
        String paramStr = "";

        if (httpExchange.getRequestMethod().equals("GET"))
        {
            paramStr = httpExchange.getRequestURI().getQuery();
        }
        else
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBodyContent = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                requestBodyContent.append(line);
            }
            paramStr = requestBodyContent.toString();
        }
        return paramStr;

    }

    /**
     * 处理响应
     */
    private void handleResponse(HttpExchange httpExchange, String responsetext) throws Exception
    {
        byte[] responseContentByte = responsetext.getBytes(StandardCharsets.UTF_8);
        //设置响应头，必须在sendResponseHeaders方法之前设置！
        httpExchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
        //设置响应码和响应体长度，必须在getResponseBody方法之前调用！
        httpExchange.sendResponseHeaders(200, responseContentByte.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }
}
