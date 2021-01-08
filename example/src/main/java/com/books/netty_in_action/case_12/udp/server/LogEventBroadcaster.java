package com.books.netty_in_action.case_12.udp.server;

import com.books.netty_in_action.case_12.udp.codec.LogEventEncoder;
import com.books.netty_in_action.case_12.udp.entity.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster {
    private final File file;
    private EventLoopGroup boss;
    private Bootstrap bootstrap;
    public LogEventBroadcaster(InetSocketAddress address, File file) {
        boss = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(boss)
                .channel(NioDatagramChannel.class)
                .remoteAddress(address)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();

        long pointer = 0;

        for(;;){
            long length = file.length();
            if(length < pointer){
                pointer = length;
            } else if(length > pointer){
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(pointer);

                String line;
                while ((line = randomAccessFile.readLine()) != null) {
                    channel.writeAndFlush(new LogEvent(null , file.getAbsolutePath(), line, -1));
                }

                pointer = randomAccessFile.getFilePointer();
                randomAccessFile.close();
            }

            try {
                Thread.sleep(1000);
            }catch (Exception e) {
                Thread.interrupted();
                break;
            }
        }
    }

    private void stop(){
        boss.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        LogEventBroadcaster logEventBroadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255",8989),new File("C:\\Users\\Administrator\\Desktop/test.txt"));
        try {
            logEventBroadcaster.run();
        }finally {
            logEventBroadcaster.stop();
        }
    }
}
