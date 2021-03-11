package com.books.netty_in_action.chapter_two.simple.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/17 15:39
 */
public class SimpleEchoClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        try {


            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 8989))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleEchoClientHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.connect().sync();
            ChannelFuture sync1 = bootstrap.connect().sync();
            ChannelFuture sync2 = bootstrap.connect().sync();
            ChannelFuture sync3 = bootstrap.connect().sync();
            ChannelFuture sync4 = bootstrap.connect().sync();
            ChannelFuture sync5 = bootstrap.connect().sync();
            ChannelFuture sync6 = bootstrap.connect().sync();
            sync.channel().closeFuture().sync();
            sync1.channel().closeFuture().sync();
            sync2.channel().closeFuture().sync();
            sync3.channel().closeFuture().sync();
            sync4.channel().closeFuture().sync();
            sync5.channel().closeFuture().sync();
            sync6.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
        }

    }
}
