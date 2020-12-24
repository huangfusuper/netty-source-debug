package com.books.netty_in_action.pre_handler.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;

public class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
        System.out.println("上一个："+msg.text());
    }
}
