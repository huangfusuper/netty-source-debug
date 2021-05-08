package com.debug.tomcat;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author 源码学徒
 * @date
 */
public class ByteBufferFileTest {
    public static void main(String[] args) throws IOException {
        RandomAccessFile inputStream = new RandomAccessFile("C:\\Users\\huangfu\\Desktop/byteBuffer.txt", "rw");
        FileChannel channel = inputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] bytes = new byte[1024];
        int readCount = 0;

        int position = 0;
        //将数据从文件写进缓冲区
        while ((readCount = channel.read(byteBuffer)) != -1) {
            //翻转写模式为读模式
            byteBuffer.flip();
            //将数据读进数组
            byteBuffer.get(bytes, 0, readCount);
            System.out.print(new String(bytes, StandardCharsets.UTF_8));
            byteBuffer.clear();
        }

        //还原指针位置
        byteBuffer.clear();
        //开始写入数据
        byteBuffer.put("\n真正的大师永远抱着学徒的心!".getBytes(StandardCharsets.UTF_8));
        //反转为读模式
        byteBuffer.flip();
        //向文件写入
        channel.write(byteBuffer);
        //刷新到磁盘  true将文件的权限信息等原信息写入    false不写
        channel.force(true);
        channel.close();
        inputStream.close();
    }
}