package com.books.nio.reactor;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * *********************************************************************
 * 多线程Echo Server反应堆
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/1/2 18:49
 */
public class MultiThreadEchoServerReactor implements Serializable {
    private static final long serialVersionUID = -5400155084530203540L;

    ServerSocketChannel serverSocketChannel = null;
    AtomicInteger next = new AtomicInteger(0);

    Selector[] selectors = new Selector[2];
    /**
     * 子反应器
     */
    SubReactor[] subReactors = null;

    public MultiThreadEchoServerReactor() throws IOException {
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8989);
        serverSocketChannel.socket().bind(socketAddress);
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);
        //第一个选择器负责监听新连接
        SelectionKey sk = serverSocketChannel.register(selectors[0], SelectionKey.OP_ACCEPT);
        //绑定一个handler
        sk.attach(new AcceptorHandler(serverSocketChannel, selectors, next));
        //每个子反应堆负责一个选择器
        SubReactor subReactor0 = new SubReactor(selectors[0]);
        SubReactor subReactor1 = new SubReactor(selectors[1]);
        //设置反应堆组
        subReactors = new SubReactor[]{subReactor0, subReactor1};
    }

    private void start(){
        Thread thread = new Thread(subReactors[0]);
        thread.setName("1111111111");
        thread.start();
        Thread thread1 = new Thread(subReactors[1]);
        thread1.setName("222222222");
        thread1.start();
    }

    public static void main(String[] args) throws IOException {
        MultiThreadEchoServerReactor multiThreadEchoServerReactor = new MultiThreadEchoServerReactor();
        multiThreadEchoServerReactor.start();

    }
}
