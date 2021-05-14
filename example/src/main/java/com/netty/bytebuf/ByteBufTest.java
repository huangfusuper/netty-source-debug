package com.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * @author huangfu
 * @date
 */
public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(32);
        byteBuf.release();
    }
}
