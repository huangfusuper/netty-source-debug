package com.books.netty_in_action.pre_codec.leng;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * *********************************************************************
 * 基于不定长消息的编解码器
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 15:27
 */
public class NettyServer implements Serializable {
    private static final long serialVersionUID = 5390901552661313546L;

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
                        //maxFrameLength 表示的是包的最大长度，超出包的最大长度netty将会做一些特殊处理，后面会讲到
                        //长度域的偏移量
                        //长度域长度
                        //要添加到长度字段值的补偿值 胞体长度
                        //initialBytesToStrip: 需要跳过的字节  比如不携带长度到下一个处理器
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                        ch.pipeline().addLast(new ByteToMessageCodec<String>() {
                            @Override
                            protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
                                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                                int  dataLength = bytes.length;
                                out.writeInt(dataLength);
                                out.writeBytes(bytes);
                            }

                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                //返回等于的可读字节数
                                int readableBytes = in.readableBytes();
                                byte[] bytes = new byte[readableBytes];
                                in.readBytes(bytes);
                                out.add(new String(bytes));
                            }
                        });
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("客户端发送："+msg);
                                ctx.writeAndFlush(msg+":-------:"+msg);

                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                            }
                        });
                    }
                });
        ChannelFuture sync = bootstrap.bind().sync();
        sync.channel().closeFuture().addListener(future -> {
            if (future.isSuccess()) {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
        });
    }
}
