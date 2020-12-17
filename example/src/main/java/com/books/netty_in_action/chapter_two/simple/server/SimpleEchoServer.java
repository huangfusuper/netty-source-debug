package com.books.netty_in_action.chapter_two.simple.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/17 12:56
 */
public class SimpleEchoServer {

    public static void main(String[] args) throws InterruptedException {
        int port = 8989;
        start(port);
    }

    /**
     * 开始节点
     * @param port 端口号
     */
    private static void start(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //服务启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //数据设置
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(group)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleEchoServerHandler());
                        }
                    });
            //端口绑定
            ChannelFuture sync = serverBootstrap.bind().sync();
            //通道关闭
            sync.channel().closeFuture().sync();
        }finally {
            //资源释放
            group.shutdownGracefully().sync();
        }
    }

}
