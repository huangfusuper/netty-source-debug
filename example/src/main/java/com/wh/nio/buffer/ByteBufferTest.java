package com.wh.nio.buffer;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * ByteBuffer测试程序
 *
 * @author huangfu
 * @date 2021年3月18日08:23:48
 */
public class ByteBufferTest {

    public static   void main() throws NoSuchFieldException, IllegalAccessException {
        //获取JDK底层的操作物理内存的工具类
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe o = (Unsafe) theUnsafe.get(null);
        //从物理内存分配一块128的内存
        long address = o.allocateMemory(128);
        System.out.println(address);
        //获取字节数组的一个基本偏移  数组基本偏移
        long arrayBaseOffset = (long)o.arrayBaseOffset(byte[].class);
        byte[] bytes = "欢迎关注公众号:【源码学徒】 学习更多源码知识！".getBytes();

        //向物理内存复制一段数据
        // 数据源     数据的基本偏移    目标数据源     要复制到的内存地址    复制数据的长度
        o.copyMemory(bytes, arrayBaseOffset, null, address, bytes.length);
        //从物理机将数据拷贝回JVM内存中
        byte[] copy = new byte[bytes.length];
        // 数据源     物理地址    目标数据源     数组基本偏移量    复制数据的长度
        o.copyMemory(null, address, copy, arrayBaseOffset, bytes.length);
        //释放内存
        o.freeMemory(address);
        System.out.println(new String(copy));
    }

    public static  void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        //分配一个堆外内存
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(128);

        byteBuffer.put("ABCDE".getBytes());
        byteBuffer.flip();
        byte[] bytes = new byte[5];
        byteBuffer.get(bytes);
        main();


    }
}
