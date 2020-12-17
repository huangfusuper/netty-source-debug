package com.books.netty_in_action.chapter_two.simple.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * *********************************************************************
 * 简单的打印程序的服务
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/17 12:32
 */
@ChannelHandler.Sharable
public class SimpleEchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            String byteBufStr = byteBuf.toString(StandardCharsets.UTF_8);
            System.out.println("客户端发送消息：" + byteBufStr);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //回复
            String reply = String.format("%s---回复时间：%s", byteBufStr, LocalDateTime.now().format(dateTimeFormatter));
            ByteBuf replyByteBuf = Unpooled.copiedBuffer(reply, StandardCharsets.UTF_8);
            //写回客户端
            ctx.write(replyByteBuf);
        }
    }

    /**
     * 将末尾消息刷新进缓冲区，写回客户端，同时写入完毕后，关闭管道
     *
     * @param ctx 上下文对象
     * @throws Exception 异常对象
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
