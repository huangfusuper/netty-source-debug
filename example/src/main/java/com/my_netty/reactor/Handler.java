package com.my_netty.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 处理器
 *
 * @author huangfu
 * @date 2021年3月12日10:15:51
 */
public class Handler implements Runnable {
    final SocketChannel socketChannel;
    static final ByteBuffer allocate = ByteBuffer.allocate(128);
    final Selector selector;

    public Handler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;

        this.selector = selector;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (selector.select(200) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        if (next.isReadable()) {
                            int read = socketChannel.read(allocate);
                            if(read > 0) {
                                System.out.print("线程" + Thread.currentThread().getName() + ":");
                                System.out.println(new String(allocate.array(), StandardCharsets.UTF_8));
                            }else if(read == -1) {
                                System.out.println("连接断开");
                            }
                            allocate.clear();
                        }

                    }
                    selectionKeys.clear();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
