package com.books.netty_in_action.pre_handler.http.server;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            UserHttp userHttp = new UserHttp("隶属于",12);
            String jsonData= JSONUtil.toJsonStr(userHttp);

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(jsonData.getBytes(StandardCharsets.UTF_8)));
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
