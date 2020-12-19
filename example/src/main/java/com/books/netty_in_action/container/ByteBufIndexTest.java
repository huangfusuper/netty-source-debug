package com.books.netty_in_action.container;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * *********************************************************************
 * Netty的数据容器
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/19 16:39
 */
public class ByteBufIndexTest {
    public static void main(String[] args) {

        markBuf();
    }

    /**
     * 标记缓存区
     */
    private static void markBuf() {
        //初始化  256个长度
        ByteBuf buffer = Unpooled.buffer();
        byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9,0};
        buffer.writeBytes(bytes);

        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        //记录当前的读指针位置
        buffer.markReaderIndex();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        buffer.readByte();
        //恢复到之前的读指针
        buffer.resetReaderIndex();
        System.out.println(buffer.readByte());

        buffer.writeByte(10);
        buffer.writeByte(11);
        //记录当前的写指针
        buffer.markWriterIndex();
        buffer.writeByte(12);
        buffer.writeByte(13);
        buffer.writeByte(14);
        buffer.resetWriterIndex();
        buffer.writeByte(15);
        System.out.println(buffer.getByte(12));
        System.out.println(buffer.getByte(13));
        print(buffer);


    }

    /**
     * 派生缓冲区
     */
    private static void deriveBuf() {
        //初始化  256个长度
        ByteBuf buffer = Unpooled.buffer();
        byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9,0};
        buffer.writeBytes(bytes);
        System.out.println("获取派生缓冲区");
        ByteBuf duplicateCopy = buffer.duplicate();
        System.out.println(buffer.getByte(0) == duplicateCopy.getByte(0));
        System.out.println("更改派生缓冲区");
        duplicateCopy.setByte(0,1);
        System.out.println(buffer.getByte(0) == duplicateCopy.getByte(0));
        System.out.println("更改派生缓冲区");
        duplicateCopy.writeByte(10);
        System.out.println("原始缓冲区"+buffer.getByte(10));
        System.out.println("截取缓冲区未 1 - 5");
        ByteBuf slice = buffer.slice(1, 5);
        System.out.println("包含1:"+slice.getByte(0));
        System.out.println("修改派生缓冲区");
        slice.setByte(0,100);
        //这里获取1 的原因是 slice是截取 1-5  所以slice更改索引0的位置 相当于其他两个的索引1位置
        System.out.println("原始缓冲区："+ buffer.getByte(1));
        System.out.println("派生缓冲区1："+duplicateCopy.getByte(1));
        System.out.println("派生缓冲区2："+slice.getByte(0));
        System.out.println("更改原始缓冲区");
        buffer.setInt(1, 10000);
        System.out.println("原始缓冲区："+ buffer.getInt(1));
        System.out.println("派生缓冲区1："+duplicateCopy.getInt(1));
        System.out.println("派生缓冲区2："+slice.getInt(0));
        System.out.println("结论： 派生缓冲区创建的缓冲区与原缓冲区共享空间和地址，但有不同的读写指针");
    }

    /**
     * 缓冲区复制
     */
    private static void copyBuf() {
        //初始化  256个长度
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(5);
        ByteBuf copy = buffer.copy();
        System.out.println(buffer == copy);


        copy.setInt(0,10);
        System.out.println("copy的缓冲区"+copy.readInt());
        System.out.println("原缓冲区"+buffer.readInt());
        System.out.println("结论： copy API创建的副本无法与源缓冲区共享");

    }

    private static void print(ByteBuf buffer) {
        //初始化  256个长度
        System.out.printf("总量: %d%n", buffer.capacity());
        System.out.printf("剩余写的容量: %d%n", buffer.capacity() - buffer.writerIndex());
        System.out.printf("剩余读的容量: %d%n", buffer.writerIndex() - buffer.readerIndex());
        System.out.printf("读指针位置: %d%n", buffer.readerIndex());
        System.out.printf("写指针位置: %d%n", buffer.writerIndex());
    }


    /**
     * get set操作
     */
    private static void getSetIndex() {
        //初始化  256个长度
        ByteBuf buffer = Unpooled.buffer();
        print(buffer);
        //初始化一个字节数组 目的是吧byteBuf填满
        //byte[] bytes = new byte[257]
        //set方法设置的字节不会自动扩容
        //buffer.setBytes(0,buffer)

        //初始化一个字节数组 目的是吧byteBuf填满
        byte[] bytes = new byte[256];
        buffer.setBytes(0, bytes);
        buffer.getBytes(0, bytes);
        print(buffer);
        System.out.println("结论：get/set操作不会自动扩容，get/set操作与指针无关 不移动");

    }

    /**
     * 读写指针的移动
     */
    private static void readWriteIndex() {
        ByteBuf buffer = Unpooled.buffer();
        print(buffer);
        //初始化一个字节数组 目的是吧butebuf填满
        byte[] bytes = new byte[257];
        //开始将字节写入缓冲区
        buffer.writeBytes(bytes);
        print(buffer);
        //读出 257各字节
        buffer.readBytes(bytes);
        print(buffer);
        System.out.println("结论：会自动扩容，写入字节会导致写指针后移！读数据会导致读指针后移");
    }


}
