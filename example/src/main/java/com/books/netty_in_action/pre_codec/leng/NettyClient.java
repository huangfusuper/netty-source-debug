package com.books.netty_in_action.pre_codec.leng;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 19:14
 */
public class NettyClient implements Serializable {
    private static final long serialVersionUID = -916185114244319689L;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup  work = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("127.0.0.1",8989))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast(new ByteToMessageCodec<String>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
                                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                                int dataLength = bytes.length;
                                out.writeInt(dataLength);
                                out.writeBytes(bytes);
                            }

                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                int i = in.readableBytes();
                                byte[] bytes = new byte[i];
                                in.readBytes(bytes);
                                out.add(new String(bytes));
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
        ChannelFuture sync = bootstrap.connect().sync();
        Channel channel = sync.channel();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            if("exit".equals(s)){
                channel.closeFuture().addListener(future -> {
                    if (future.isSuccess()) {
                        work.shutdownGracefully();
                    }
                });
                return;
            }

            channel.writeAndFlush(s).sync().addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("消息发送成功");
                }
            });
        }
    }
}
