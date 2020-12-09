package com.debug;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioTest {

    public static void main(String[] args) throws IOException {
        SelectorProvider provider = SelectorProvider.provider();
        ServerSocketChannel serverSocketChannel = provider.openServerSocketChannel();
        serverSocketChannel.configureBlocking(false);
        Selector open = Selector.open();
        SelectionKey register = serverSocketChannel.register(open, 0);
        System.out.println(register.interestOps());
        register.interestOps(SelectionKey.OP_ACCEPT);
        System.out.println(register.interestOps());
    }
}
