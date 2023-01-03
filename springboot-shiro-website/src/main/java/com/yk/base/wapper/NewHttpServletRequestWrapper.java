package com.yk.base.wapper;

import cn.hutool.core.io.IoUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    private final byte[] body;

    public NewHttpServletRequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);
        body = IoUtil.readBytes(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream is = new ByteArrayInputStream(body);

        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return is.read();
            }

            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {
            }
        };
    }
}
