package com.books.netty_in_action.pre_handler.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyWebsocketServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bos = new NioEventLoopGroup(1);
        EventLoopGroup wor = new NioEventLoopGroup();


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bos, wor)
                .channel(NioServerSocketChannel.class)
                .localAddress(8989)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        //为握手提供消息聚合
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        //Web套接字服务器协议处理程序
                        pipeline.addLast(new WebSocketServerProtocolHandler("/huangfu"));
                        //文本处理程序
                        pipeline.addLast(new WebSocketTextFrameHandler());
                        //二进制处理程序
                        pipeline.addLast(new BinaryFrameHandler());
                        //上一个
                        pipeline.addLast(new ContinuationFrameHandler());
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().addListener(future -> {
            if (future.isSuccess()) {
                bos.shutdownGracefully();
                wor.shutdownGracefully();
            }
        });
    }
}
