package com.books.netty_in_action.case_12.udp.codec;

import com.books.netty_in_action.case_12.udp.entity.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author huangfu
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent msg, List<Object> out) throws Exception {
        byte[] fileBytes = msg.getLogFile().getBytes(StandardCharsets.UTF_8);
        byte[] msgBytes = msg.getMsg().getBytes(StandardCharsets.UTF_8);

        ByteBuf buffer = ctx.alloc().buffer(fileBytes.length + msgBytes.length+1);
        buffer.writeBytes(fileBytes);
        buffer.writeByte(LogEvent.SEPARATOR);
        buffer.writeBytes(msgBytes);
        out.add(new DatagramPacket(buffer, remoteAddress));
    }
}
