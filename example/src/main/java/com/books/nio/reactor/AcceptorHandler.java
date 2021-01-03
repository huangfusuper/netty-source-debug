package com.books.nio.reactor;

import java.io.Serializable;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/1/2 19:03
 */
public class AcceptorHandler implements Serializable,Runnable {
    private static final long serialVersionUID = -2417686393491624493L;

    ServerSocketChannel serverSocketChannel;
    AtomicInteger next;
    Selector[] selectors;

    public AcceptorHandler(ServerSocketChannel serverSocketChannel, Selector[] selectors, AtomicInteger next) {
        this.serverSocketChannel = serverSocketChannel;
        this.next = next;
        this.selectors = selectors;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel != null) {
                new MultiThreadEchoHandler(selectors[next.get()], socketChannel);
            }
            if(next.incrementAndGet() == selectors.length){
                System.out.println("------------充值");
                next.set(0);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
