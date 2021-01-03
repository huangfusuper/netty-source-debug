package com.books.nio.reactor;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *********************************************************************
 * 多线程回显处理程序
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/1/2 19:20
 */
public class MultiThreadEchoHandler implements Serializable,Runnable {
    private static final long serialVersionUID = -6962984282376977248L;
    private final SelectionKey selectionKey;
    SocketChannel socketChannel;
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    static final int REC = 0,SEN =1;
    int state = REC;
    public MultiThreadEchoHandler(Selector selector, SocketChannel c) throws IOException {
        this.socketChannel = c;
        c.configureBlocking(false);
        //不关注任何事件
        selectionKey = this.socketChannel.register(selector, 0);
        //绑定对客户端的处理器
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        executorService.execute(this::asyncRun);
    }

    public synchronized void asyncRun(){
        try {
            if(state ==SEN){
                //写入通道
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
                //注册读事件
                selectionKey.interestOps(SelectionKey.OP_READ);
                //进入接受状态
                state = REC;
            } else if(state == REC){
                //从通道读
                int length = 0;
                while ((length = socketChannel.read(byteBuffer)) > 0){
                    System.out.println("读出"+length+"个字节。数据为:"+ new String(byteBuffer.array()));
                }
                //读完后 注册写事件  准备写
                byteBuffer.flip();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SEN;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
