package com.codecs.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * *********************************************************************
 * 欢迎关注公众号: 【源码学徒】
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/5/6 21:22
 */
public class CodecServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
            ByteBufAllocator aDefault = ByteBufAllocator.DEFAULT;
            ByteBuf byteBuf = aDefault.directBuffer();
            byte[] bytes = "无论是任何的源码学习，永远都是枯燥、乏味的，他远没有写出一段很牛逼的代码有成就感！但是当你登堂入室的那一刻，你会发现，源码的阅读是如此的享受！".getBytes(StandardCharsets.UTF_8);
            byteBuf.writeBytes(bytes);
            ctx.writeAndFlush(byteBuf);
        }, 10, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
