package com.books.netty_in_action.codec.client;

import com.books.netty_in_action.codec.MyByteToMessage;
import com.books.netty_in_action.codec.MyMessageToMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(boss).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new MyByteToMessage());
                ch.pipeline().addLast(new MyMessageToMessage());
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        bootstrap.remoteAddress(new InetSocketAddress("127.0.0.1",8989));
        ChannelFuture sync = bootstrap.connect().sync();
        ChannelFuture sync1 = bootstrap.connect().sync();
        ChannelFuture sync2 = bootstrap.connect().sync();
        ChannelFuture sync3 = bootstrap.connect().sync();
        sync.channel().closeFuture().sync().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
            }
        });
        sync1.channel().closeFuture().sync().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
            }
        });
        sync2.channel().closeFuture().sync().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
            }
        });
        sync3.channel().closeFuture().sync().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
            }
        });
    }
}
