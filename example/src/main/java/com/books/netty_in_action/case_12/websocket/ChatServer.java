package com.books.netty_in_action.case_12.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {
    private final ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup work = new NioEventLoopGroup();
    private Channel channel;


    private ChannelFuture start(InetSocketAddress address){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChatServerInitializer(channels));
        ChannelFuture channelFuture = serverBootstrap.bind(address);
        //等待这个将来直到完成，如果这个将来失败，则重新抛出失败原因。
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
        return channelFuture;
    }

    public void destroy(){
        if(channel!=null){
            channel.close();
        }
        channels.close();
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        ChannelFuture start = chatServer.start(new InetSocketAddress(8989));
        start.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("启动成功");
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(chatServer::destroy));
        start.channel().closeFuture().syncUninterruptibly();
    }
}
