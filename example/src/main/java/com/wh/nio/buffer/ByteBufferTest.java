package com.wh.nio.buffer;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * ByteBuffer测试程序
 *
 * @author huangfu
 * @date 2021年3月18日08:23:48
 */
public class ByteBufferTest {

    public static void main(String[] args) {
        //分配一个堆内存
        ByteBuffer heapRam = ByteBuffer.allocate(128);
        //分配一个堆外内存
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(128);
        //转换字节为堆内内存
        ByteBuffer wrapBuffer = ByteBuffer.wrap("huangfu".getBytes());
        heapRam.put("1".getBytes());
        heapRam.put("2".getBytes());
        heapRam.put("3".getBytes());
        heapRam.put("4".getBytes());
        heapRam.flip();

        byte b1= heapRam.get();
        byte b2 = heapRam.get();
        byte b3 = heapRam.get();
        byte b4 = heapRam.get();

        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);
        System.out.println(b4);
    }
}
