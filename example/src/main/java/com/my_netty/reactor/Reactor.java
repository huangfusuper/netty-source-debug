package com.my_netty.reactor;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 反应器
 *
 * @author huangfu
 * @date 2021年3月12日10:15:14
 */
public class Reactor implements Runnable {
    private final Selector selector;

    public Reactor(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            System.out.println("服务启动成功");
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {

                    SelectionKey next = iterator.next();
                    iterator.remove();
                    dispatch(next);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey next) {
        Runnable attachment = (Runnable) next.attachment();
        if(attachment!=null) {
            attachment.run();
        }
    }
}
