package com.yk.core;

/**
 * 服务请求接口
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 13:33
 */
public interface IExecutorService
{
    Response execute(Request request);

    String getType();
}
