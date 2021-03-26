package com.ssh;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * @author huangfu
 * @date
 */
public class SshClent {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress("10.0.55.79",22)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SshHandler());
                    }
                });
        ChannelFuture sync = bootstrap.connect().sync();
        Channel channel = sync.channel();
        ByteBufAllocator alloc = channel.alloc();
        ByteBuf buffer = alloc.buffer();
        buffer.writeByte((byte)50);
        buffer.writeBytes("root".getBytes(StandardCharsets.UTF_8));
        buffer.writeBytes("ssh-connection".getBytes(StandardCharsets.UTF_8));
        buffer.writeBytes("password".getBytes(StandardCharsets.UTF_8));
        buffer.writeByte((byte)0);
        buffer.writeBytes("123456".getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(buffer);
        channel.writeAndFlush("ls /");
        channel.closeFuture().sync();
    }
}
