package com.books.netty_in_action.pre_codec.fixed;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * *********************************************************************
 * 基于定长解码器的解码器
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 15:09
 */
public class NettyServerHandler implements Serializable {


    private static final long serialVersionUID = -7825135234590598792L;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,work)
                .localAddress(8989)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(8));
                        ch.pipeline().addLast(new ByteToMessageDecoder() {
                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                ByteBuf byteBuf = in.readBytes(8);
                                out.add(byteBuf.toString(StandardCharsets.UTF_8));
                            }
                        });

                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println(msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                            }
                        });
                    }
                });
        ChannelFuture bindFunr = serverBootstrap.bind().sync();
        bindFunr.channel().closeFuture().sync().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        });

    }
}
