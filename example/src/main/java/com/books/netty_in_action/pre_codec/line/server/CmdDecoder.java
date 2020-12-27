package com.books.netty_in_action.pre_codec.line.server;

import com.books.netty_in_action.pre_codec.line.entiry.CmdEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * *********************************************************************
 * 命令解码器，基于换行符的解码器
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 14:14
 */
public class CmdDecoder extends LineBasedFrameDecoder implements Serializable {
    private static final long serialVersionUID = 5735573398755729111L;

    public CmdDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        String toString = buffer.toString(StandardCharsets.UTF_8);
        ByteBuf decode = (ByteBuf) super.decode(ctx, buffer);
        if(decode == null){
            return null;
        }

        String[] s = toString.split(" ");

        return new CmdEntity(s[0],s[1]);
    }
}
