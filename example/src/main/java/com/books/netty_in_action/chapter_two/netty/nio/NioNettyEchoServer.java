package com.books.netty_in_action.chapter_two.netty.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * BIO Netty时间
 *
 * @author huangfu
 * @date 2020年12月18日16:31:11
 */
public class NioNettyEchoServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup nioGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(nioGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8989)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                final ByteBuf byteBuf = Unpooled.copiedBuffer("Hi Netty Nio".getBytes(StandardCharsets.UTF_8));

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {


                                    ctx.writeAndFlush(byteBuf.duplicate()).addListener((ChannelFutureListener) future -> {
                                        if (future.isSuccess()) {
                                            System.out.println("yes");
                                        }
                                    });
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("---------");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println("------异常了---------");
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            nioGroup.shutdownGracefully().sync();
        }
    }
}
