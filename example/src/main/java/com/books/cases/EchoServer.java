package com.books.cases;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 打印执行器
 *
 * @author huangfu
 * @date 2021年1月21日08:40:14
 */
public class EchoServer {
    public static void main(String[] args) throws InterruptedException {
        //配置 EventLoopGroup 线程组；
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,work)
                    //配置 Channel 的类型；
                    .channel(NioServerSocketChannel.class)
                    //设置 ServerSocketChannel 对应的 Handler；
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    //设置网络监听的端口；
                    .localAddress(8080)
                    //设置 SocketChannel 对应的 Handler；
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    System.out.println(byteBuf.toString(StandardCharsets.UTF_8));
                                }
                            });
                        }
                    });
            //bind() 才是真正进行服务器端口绑定和启动的入口，sync() 表示阻塞等待服务器启动完成。
            //重点
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }
}
