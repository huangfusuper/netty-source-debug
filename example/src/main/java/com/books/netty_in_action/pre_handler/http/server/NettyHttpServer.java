package com.books.netty_in_action.pre_handler.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.net.ssl.SSLException;
import java.security.NoSuchAlgorithmException;

public class NettyHttpServer {

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, SSLException {
        EventLoopGroup bos = new NioEventLoopGroup(1);
        EventLoopGroup wor = new NioEventLoopGroup();
        //SslContext sslContext = SslContextBuilder.forServer().build();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bos, wor).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline pipeline = ch.pipeline();
                        //ssl认证 https
                        //pipeline.addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
                        //http 请求响应编解码
                        pipeline.addLast(new HttpServerCodec());
                        //http 消息聚合器
                        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
                        //Http内容压缩器 通过请求的请求头 content-encoding 指定
                        pipeline.addLast(new HttpContentCompressor());
                        //请求响应处理器
                        pipeline.addLast(new NettyHttpServerHandler());
                    }
                });
        serverBootstrap.localAddress(8989);
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().addListener(future -> {
            if (future.isSuccess()) {
                bos.shutdownGracefully();
                wor.shutdownGracefully();
            }
        });
    }

}
