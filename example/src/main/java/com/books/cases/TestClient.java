package com.books.cases;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/15 10:19
 */
public class TestClient implements Serializable {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(boss)
                .remoteAddress(new InetSocketAddress(8080))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf byteBuf = Unpooled.copiedBuffer("test thread:", CharsetUtil.UTF_8).retain();
                                int x = 1;
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        ctx.writeAndFlush(byteBuf.copy());
                                        System.out.println(Thread.currentThread().getName() + " " + byteBuf);
                                    }
                                };
                                Executor executor = Executors.newCachedThreadPool();
                                for(int i = 0; i <= 10; i++){
                                    executor.execute(runnable);
                                }
                            }
                        });
                    }
                });
        bootstrap.connect().sync().channel().closeFuture().sync();

    }
}
