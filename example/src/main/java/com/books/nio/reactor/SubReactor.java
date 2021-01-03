package com.books.nio.reactor;

import java.io.Serializable;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * *********************************************************************
 * 子反应堆
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/1/2 18:52
 */
public class SubReactor implements Serializable,Runnable {
    private static final long serialVersionUID = -1400848677812521976L;

    Selector selector;

    public SubReactor(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey);
                }
                selectionKeys.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        //获取绑定的处理器
        Runnable attachment = (Runnable) selectionKey.attachment();
        if(attachment != null){
            attachment.run();
        }
    }
}
