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
        socketChannel.configureBlocking(false);
        key = socketChannel.register(selector, 0);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {

        try{
            if (key.isReadable()) {
                read();
                key.interestOps(SelectionKey.OP_WRITE);
            }else if(key.isWritable()){
                // 2. 分配缓存区
                ByteBuffer buffer = ByteBuffer.allocate(128);
                // 3. 清空缓存区
                buffer.clear();
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
