/*
package com.netty;

import cn.hutool.core.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpRequest
{
    public void post()
    {
        // Prepare the HTTP request.
        String host = "127.0.0.1";
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());

        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaders.Values.KEEP_ALIVE); // or HttpHeaders.Values.CLOSE
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");
        ByteBuf bbuf = Unpooled.copiedBuffer("{\"jsonrpc\":\"2.0\",\"method\":\"calc.add\",\"params\":[1,2],\"id\":1}", StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
        request.content().clear().writeBytes(bbuf);

        channel.writeAndFlush(request);
    }

    public void post2()
    {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.content().writeBytes("".getBytes(StandardCharsets.UTF_8));
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        request.setUri(uri.toString());
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }
}
*/
