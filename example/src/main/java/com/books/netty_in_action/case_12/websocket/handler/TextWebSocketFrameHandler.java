package com.books.netty_in_action.case_12.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * websocket文本处理器
 *
 * @author huangfu
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup channels;
    public TextWebSocketFrameHandler(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //增加消息的引用计数，并写入到所有的客户端
        channels.writeAndFlush(msg.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channels.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " 下线"));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //升级为 websocket 移除对于http的支持  保存管道流
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            ctx.pipeline().remove(HttpRequestHandler.class);
            channels.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " 上线"));
            channels.add(ctx.channel());
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }
}
