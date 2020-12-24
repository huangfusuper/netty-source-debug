package com.books.netty_in_action.pre_handler.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.TimeUnit;

/**
 * web socket 的文本处理程序
 */
public class WebSocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println(msg.text());
        ctx.executor().scheduleAtFixedRate(() ->{
            ctx.writeAndFlush(new TextWebSocketFrame("你好"));
        },0,1, TimeUnit.SECONDS);

    }
}
