package com.yk.auth;

import com.yk.connector.http.HttpRequest;
import com.yk.core.Response;
import com.yk.core.CommonInfo;
import com.yk.core.SdkExecutors;
import com.yk.event.InitializingEvent;
import com.yk.event.InitializingListener;
import com.yk.exception.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.yk.connector.http.HttpRequest.LOGIN_URI;

/**
 * 登录 缓存cookie
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/28 10:06
 */
public class LoginAuth implements InitializingListener
{
    private static final Logger logger = LoggerFactory.getLogger(LoginAuth.class);

    private static final long COOKIE_TIMEOUT = 10 * 60 * 1000;

    private final long loginTime = System.currentTimeMillis();

    private volatile String authorization;

    private volatile CommonInfo info;

    public static LoginAuth INSTANCE = new LoginAuth();

    public String getAuthorization()
    {
        if (null == authorization || System.currentTimeMillis() - loginTime > COOKIE_TIMEOUT)
        {
            synchronized (this)
            {
                if (null == authorization || System.currentTimeMillis() - loginTime > COOKIE_TIMEOUT)
                {
                    login();
                }
            }
        }
        return authorization;
    }

    private LoginAuth()
    {
    }

    public synchronized void login()
    {
        Map<String, Object> params = new HashMap<>();
        params.put("user", "user");
        params.put("passwd", "passwd");
        Response response = SdkExecutors.create()
                .execute(HttpRequest.<Map<String, String>>create().uri(LOGIN_URI)
                        .method("POST")
                        .params(params).async()
                        .build());
        if (null == response.getHttpResult())
        {
            logger.error("login failed {}", response.getHttpResult());
            logger.error("login failed {}", response.getHeaders());
            logger.error("login failed {}", response.getStatus());
            throw new SdkException("服务登录失败");
        }
        String result = response.getHttpResult();
    }

    @Override
    public void onInitializing(InitializingEvent message)
    {
        if (message.getType().equalsIgnoreCase(LoginAuth.class.getName()))
        {
            INSTANCE.info = message.getInfo();
        }
    }
}