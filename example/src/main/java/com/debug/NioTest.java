package com.debug;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioTest {
    private static ServerSocketChannel serverSocketChannel;

    public static void main(String[] args) throws IOException {
        init();
        SelectionKey selectionKey = registered();
        bind(selectionKey);
        System.out.println(selectionKey.interestOps());
    }

    public static void bind(SelectionKey selectionKey) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(8887));
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
    }

    /**
     * 注册
     * @return
     * @throws IOException
     */
    public static SelectionKey registered() throws IOException {
        Selector open = Selector.open();
        return serverSocketChannel.register(open, 0);
    }

    /**
     * 初始化方法
     * @throws IOException
     */
    private static void init() throws IOException {
        SelectorProvider provider = SelectorProvider.provider();
        ServerSocketChannel serverSocketChannel = provider.openServerSocketChannel();
        serverSocketChannel.configureBlocking(false);
        NioTest.serverSocketChannel = serverSocketChannel;
    }

}
