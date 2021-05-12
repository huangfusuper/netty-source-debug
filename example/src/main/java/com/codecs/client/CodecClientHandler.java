package com.codecs.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * *********************************************************************
 * 欢迎关注公众号: 【源码学徒】
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/5/6 21:31
 */
public class CodecClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String msgStr = (String) msg;
        System.out.println(msgStr);
        super.channelRead(ctx, msg);
    }
}
