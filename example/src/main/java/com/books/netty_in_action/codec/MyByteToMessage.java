package com.books.netty_in_action.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 数据编解码器
 *
 */
public class MyByteToMessage extends ByteToMessageCodec<Integer> {

    /**
     * 数据编码
     * @param ctx
     * @param msg 具体的消息数据
     * @param out 转换完的数据
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        System.out.println("-------数据编码，将" + msg + "编码为bytebuf");
        out.writeInt(msg);
    }

    /**
     * 数据解码
     * @param ctx
     * @param in 将要 解码的数据
     * @param out 解码完毕的数据
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int e = in.readInt();
        System.out.println("-------数据解码，将bytebuf解码为：" + e);

        out.add(e);
    }
}
