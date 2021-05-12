package com.codecs.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * *********************************************************************
 * 自定义一个基于固定长度的解码器，当解码成功后，将数据转成字符串
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/5/7 22:43
 */
public class MessageEqualDecoder extends ByteToMessageDecoder {
    private Integer length;

    public MessageEqualDecoder(Integer length) {
        this.length = length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //当前的可读字节数
        int readableBytes = in.readableBytes();
        if(readableBytes >= length) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            out.add(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
