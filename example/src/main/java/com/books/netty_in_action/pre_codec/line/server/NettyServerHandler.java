package com.books.netty_in_action.pre_codec.line.server;

import com.books.netty_in_action.pre_codec.line.entiry.CmdEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 14:06
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<CmdEntity> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmdEntity msg) throws Exception {
        System.out.println("cmd:"+msg.getCmd());
        System.out.println("args:"+msg.getArgs());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
