package com.books.netty_in_action.codec.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println((String)msg);
        ChannelFuture sync = ctx.writeAndFlush("500000").sync();
        sync.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("--------------------------------------");
            }
        });
    }
}
