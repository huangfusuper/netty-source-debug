package com.my_netty.reactor;

import com.my_netty.group.SelectorGroup;
import com.my_netty.group.ThreadContext;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 连接器
 *
 * @author huangfu
 * @date 2021年3月12日10:15:37
 */
public class Acceptor implements Runnable {
    private final ServerSocketChannel serverSocketChannel;
    private final SelectorGroup selectorGroup;

    public Acceptor(ServerSocketChannel serverSocketChannel, SelectorGroup selectorGroup) {
        this.serverSocketChannel = serverSocketChannel;
        this.selectorGroup = selectorGroup;
    }


    @Override
    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            Selector next = selectorGroup.next();
            //next.wakeup();
            socketChannel.configureBlocking(false);
            SelectionKey register = socketChannel.register(next, 0);
            register.interestOps(SelectionKey.OP_READ);
            System.out.println("检测到连接：" + socketChannel.getRemoteAddress());
            if(ThreadContext.runSelect.add(next)){
                new Thread(new Handler(socketChannel,next)).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
