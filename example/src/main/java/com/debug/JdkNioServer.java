package com.debug;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class JdkNioServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8889),1024);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("-------------服务端启动成功--------------");


        new Thread(() ->{
            while (true) {
                try {
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    SelectionKey selectionKey = null;
                    while (iterator.hasNext()) {
                       try {
                           selectionKey = iterator.next();
                           iterator.remove();
                           SelectableChannel channel = selectionKey.channel();
                           if(selectionKey.isValid() && selectionKey.isAcceptable()){
                               ServerSocketChannel serverChannel = (ServerSocketChannel) channel;
                               SocketChannel accept = serverChannel.accept();
                               accept.configureBlocking(false);
                               accept.register(selectionKey.selector(),SelectionKey.OP_READ);

                           }else if(selectionKey.isValid() && selectionKey.isReadable()){
                               SocketChannel socketChannel = (SocketChannel) channel;
                               ByteBuffer allocate = ByteBuffer.allocate(1024);
                               socketChannel.read(allocate);
                               allocate.put(String.valueOf(System.currentTimeMillis()).getBytes("UTF-8"));
                               allocate.flip();
                               socketChannel.write(allocate);
                           }
                       }catch (Exception e) {
                           if (selectionKey != null) {
                               selectionKey.cancel();
                               selectionKey.channel().close();
                           }
                       }

                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"NIO SERVER").start();
    }
}
