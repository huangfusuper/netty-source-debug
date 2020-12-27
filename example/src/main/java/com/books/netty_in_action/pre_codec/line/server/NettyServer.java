package com.books.netty_in_action.pre_codec.line.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.Serializable;

/**
 * *********************************************************************
 * 基于换行符解码器的解码器
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 14:12
 */
public class NettyServer implements Serializable {
    private static final long serialVersionUID = 5834016329314173749L;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .localAddress(8989)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new CmdDecoder(Integer.MAX_VALUE));
                        ch.pipeline().addLast(new NettyServerHandler());

                    }
                });
        ChannelFuture bindFuture = bootstrap.bind().sync();
        ChannelFuture channelFuture = bindFuture.channel().closeFuture().sync();
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        });

    }
}
