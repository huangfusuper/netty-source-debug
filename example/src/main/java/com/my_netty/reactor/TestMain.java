package com.my_netty.reactor;

import com.my_netty.group.SelectorGroup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author huangfu
 * @date
 */
public class TestMain {

    public static void main(String[] args) throws IOException {
        SelectorGroup selectorGroup = new SelectorGroup(3);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        serverSocketChannel.configureBlocking(false);

        Acceptor acceptor = new Acceptor(serverSocketChannel, selectorGroup);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT,acceptor);
        serverSocketChannel.bind(new InetSocketAddress(8989));
        new Reactor(selector).run();
    }
}
