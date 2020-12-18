package com.books.netty_in_action.chapter_two.jdk.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 打印服务器
 *
 * @author huangfu
 * @date 2020年12月18日10:49:46
 */
public class NioEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8989));

        //选择器建立
        Selector selector = Selector.open();
        //注册选择器  并且关注连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //准备数据
        ByteBuffer byteBuffer = ByteBuffer.wrap("Hi Nio".getBytes(StandardCharsets.UTF_8));
        for (; ; ) {
            int select = selector.select();
            if (select > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    SelectableChannel channel = next.channel();
                    try {
                        if (next.isAcceptable()) {
                            serverSocketChannel = (ServerSocketChannel) channel;
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            System.out.println(String.format("新连接接入：%s", socketChannel));
                            //重新给新连接注册事件  设置新连接关注的时间为读写时间 绑定数据
                            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, byteBuffer);
                        }

                        if (next.isWritable()) {
                            SocketChannel socketChannel = (SocketChannel) channel;
                            ByteBuffer attachment = (ByteBuffer) next.attachment();
                            while (attachment.hasRemaining()) {
                                if (socketChannel.write(attachment) == 0) {
                                    break;
                                }
                            }

                            socketChannel.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        next.cancel();
                        next.channel().close();
                    }

                }
            }
        }

    }
}
