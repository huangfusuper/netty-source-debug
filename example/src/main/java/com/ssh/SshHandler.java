package com.ssh;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author huangfu
 * @date
 */
public class SshHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        System.out.println("-----连接成功后，服务器会返回协议版本-----");
        System.out.println(buffer.toString(StandardCharsets.UTF_8));
        super.channelRead(ctx, msg);
    }
}
