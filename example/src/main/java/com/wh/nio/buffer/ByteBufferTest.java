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

        heapRam.mark();
        byte b1= heapRam.get();
        byte b2 = heapRam.get();
        byte b3 = heapRam.get();
        byte b4 = heapRam.get();

        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);
        System.out.println(b4);

        heapRam.reset();
        byte b11= heapRam.get();
        byte b22 = heapRam.get();
        byte b33 = heapRam.get();
        byte b44 = heapRam.get();
        System.out.println(b11);
        System.out.println(b22);
        System.out.println(b33);
        System.out.println(b44);
        heapRam.clear();
        byte[] bytes = "B".getBytes();
        heapRam.put(4, bytes[0]);
        heapRam.get();
        heapRam.get();
        heapRam.get();
        heapRam.get();
        byte b5 = heapRam.get();
        System.out.println(b5);
    }
}
