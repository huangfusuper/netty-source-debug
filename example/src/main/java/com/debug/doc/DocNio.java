package com.debug.doc;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/3/10 20:34
 */
public class DocNio {
    public static void main(String[] args) throws IOException {
        //开启服务端Socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8098));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //开启一个选择器
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 阻塞等待需要处理的事件发生
            selector.select();
            // 获取selector中注册的全部事件的 SelectionKey 实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //获取已经准备完成的key
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                //当发现连接事件
                if(next.isAcceptable()) {
                    //获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置非阻塞
                    socketChannel.configureBlocking(false);
                    //将该客户端连接注册进选择器 并关注读事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    //如果是读事件
                }else if(next.isReadable()){
                    ByteBuffer allocate = ByteBuffer.allocate(128);
                    //获取与此key唯一绑定的channel
                    SocketChannel channel = (SocketChannel) next.channel();
                    //开始读取数据
                    int read = channel.read(allocate);
                    if(read > 0){
                        System.out.println(new String(allocate.array()));
                    }else if(read == -1){
                        System.out.println("断开连接");
                        channel.close();
                    }
                }
                //删除这个事件
                iterator.remove();
            }
        }
    }
}
