package com.yk.test.restful;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * 推荐使用
 * //https://github.com/apache/cxf/blob/540bb76f6f3d3d23944c566905f9f395c6f86b79/systests/transports/src/test/java/org/apache/cxf/systest/https/conduit/KeyPasswordCallbackHandler.java
 */
public class KeyManagerPassworkAuthHandler implements CallbackHandler
{
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {
        for (Callback callback : callbacks)
        {
            if (null == callback || !(callback instanceof PasswordCallback))
            {
                return;
            }
            PasswordCallback passwordCallback = (PasswordCallback) callback;
            //可以在这里使用密文，解密后set，避免代码或者配置文件中出现明文
            passwordCallback.setPassword("Admin@123".toCharArray());
        }
    }
}
