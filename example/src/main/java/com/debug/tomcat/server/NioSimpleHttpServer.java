package com.debug.tomcat.server;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 模拟一个http服务器
 *
 * @author huangfu
 * @date 2020年12月15日21:04:35
 */
public class NioSimpleHttpServer {

    private final int port;
    private final Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public NioSimpleHttpServer(int port) throws IOException {
        this.port = port;
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public Thread start(){

        return new Thread(() ->{
            while (true) {
                try {
                    dispatch();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void dispatch() throws IOException {
        selector.select(500);
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey next = iterator.next();
            iterator.remove();
            if(!next.isValid()){
                continue;
            }

            if(next.isAcceptable()) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }

            if(next.isReadable()) {
                SocketChannel channel = (SocketChannel) next.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (channel.read(byteBuffer) > 0) {
                    byteBuffer.flip();
                    out.write(byteBuffer.array(),0,byteBuffer.limit());
                    byteBuffer.clear();
                }
            }
         }
    }
    













}
