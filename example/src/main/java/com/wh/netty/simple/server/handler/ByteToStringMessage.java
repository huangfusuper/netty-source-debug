package com.wh.netty.simple.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 字节转字符串解码器
 *
 * @author huangfu
 * @date 2021年3月18日07:56:26
 */
public class ByteToStringMessage extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 5) {
            byte[] bytes = new byte[5];
            in.readBytes(bytes);
            out.add(new String(bytes));
        }

    }
}
