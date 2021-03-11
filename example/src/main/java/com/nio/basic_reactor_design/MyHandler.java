package com.nio.basic_reactor_design;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author huangfu
 * @date
 */
public class MyHandler implements Runnable {
    ByteBuffer input = ByteBuffer.allocate(256);

    final SocketChannel socketChannel;
    final SelectionKey key;
    final Selector selector;
    public MyHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.selector = selector;
        //将客户端连接设置为非阻塞
        socketChannel.configureBlocking(false);
        //注册发哦选择器 不关注任何事件
        key = socketChannel.register(selector, 0);
        //绑定当前的处理器
        key.attach(this);
        //关注读事件
        key.interestOps(SelectionKey.OP_READ);
        //开始下次循环
        selector.wakeup();
    }

    @Override
    public void run() {

        try{
            //如果为读事件
            if (key.isReadable()) {
                read();
                //设置为写事件
                key.interestOps(SelectionKey.OP_WRITE);
            }else if(key.isWritable()){
                // 2. 分配缓存区
                ByteBuffer buffer = ByteBuffer.allocate(128);
                // 4. 写入发送的信息
                buffer.put(ByteBuffer.wrap("客户端返回".getBytes(StandardCharsets.UTF_8)));
                // 5. 将缓存区的指针回到初始位置
                buffer.flip();
                //注销这个监控key 未来不再监控这个通道
                //key.cancel();
                socketChannel.write(buffer);
                key.interestOps(SelectionKey.OP_READ);
            }


        }catch (IOException e) {}
    }

    private void read() throws IOException {
        int read = socketChannel.read(input);
        if(read > 0){
            System.out.println(new String(input.array()));
            input.clear();
        }
    }
}
