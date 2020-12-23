package com.books.netty_in_action.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 消息转换器
 */
public class MyMessageToMessage extends MessageToMessageCodec<Integer,String> {


    //将字符串解码为Integer 向下传递
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        System.out.println("消息编码，将数据从String转换为Integer：");
        out.add(Integer.valueOf(msg));
    }


    //将整整解码为字符串 向下传递
    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        System.out.println("消息解码，将数据从Integer转换为String：");
        out.add(String.valueOf(msg));
    }
}
