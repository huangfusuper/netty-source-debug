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
            sync.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
        }

    }
}
