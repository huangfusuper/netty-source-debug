package com.books.netty_in_action.codec.server;

import com.books.netty_in_action.codec.MyByteToMessage;
import com.books.netty_in_action.codec.MyMessageToMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(3);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work)
                .localAddress(8989)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MyByteToMessage());
                        ch.pipeline().addLast(new MyMessageToMessage());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
        ChannelFuture sync = bootstrap.bind().sync();
        ChannelFuture channelFuture = sync.channel().closeFuture().sync();

        channelFuture.addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        });

    }
}
