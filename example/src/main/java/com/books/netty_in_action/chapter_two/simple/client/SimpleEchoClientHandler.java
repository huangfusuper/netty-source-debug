package com.books.netty_in_action.chapter_two.simple.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/17 15:35
 */
public class SimpleEchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println(msg.toString(StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("链接出现异常");
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链接通道建立成功");
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好啊",StandardCharsets.UTF_8));
    }
}
