package com.yk.connector.http;

import com.yk.core.ExecutorListener;
import com.yk.core.Response;

/**
 * 用户自定义回调
 *
 * @author yangk
 * @version 1.0
 * @since 2021/6/7 10:21
 */
public interface HttpExecutorListener extends ExecutorListener
{
    void onHttpFinishListener(Response response);
}
