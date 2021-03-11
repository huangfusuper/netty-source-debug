package com.nio.basic_reactor_design;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 主要负责新连接接入
 *
 * @author huangfu
 * @date 2021年3月11日12:22:53
 */
public class Acceptor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public Acceptor( Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }


    @Override
    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            new MyHandler(socketChannel, selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
