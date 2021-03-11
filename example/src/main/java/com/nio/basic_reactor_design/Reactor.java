package com.nio.basic_reactor_design;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 反应器
 *
 * @author huangfu
 * @date 2021年3月11日12:18:43
 */
public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        //开启一个选择器
        selector = Selector.open();
        //开启一个服务
        serverSocketChannel = ServerSocketChannel.open();
        //绑定一个服务
        serverSocketChannel.bind(new InetSocketAddress(port));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //将该服务注册到选择器上  关注连接事件 并绑定处理连接的处理器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(selector, serverSocketChannel));
    }

    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(8989);
        //开始运行反应堆
         new Thread(reactor).start();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                //阻塞等待
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    //分发数据
                    dispatch(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey next) {
        //获取绑定的处理器
        Runnable attachment = (Runnable) next.attachment();
        if(attachment != null) {
            //运行处理器
            attachment.run();
        }
    }
}
