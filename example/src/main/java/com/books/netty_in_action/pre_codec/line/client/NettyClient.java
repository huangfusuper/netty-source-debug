package com.books.netty_in_action.pre_codec.line.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.LineEncoder;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 14:27
 */
public class NettyClient implements Serializable {
    private static final long serialVersionUID = 2400133786550319707L;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup work = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8989))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MessageToByteEncoder<String>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
                                String format = String.format("%s\r\n", msg);
                                out.writeBytes(format.getBytes(StandardCharsets.UTF_8));
                            }
                        });
                    }
                });

        ChannelFuture connect = bootstrap.connect().sync();
        Channel channel = connect.channel();

        while (true) {
            Scanner sc = new Scanner(System.in);
            String nextLine = sc.nextLine();
            if(nextLine.equals("exit")){
                channel.closeFuture().sync().addListener(future -> {
                    if (future.isSuccess()) {
                        work.shutdownGracefully();
                    }
                });
                return;
            }
            channel.writeAndFlush(nextLine);
        }

    }
}
