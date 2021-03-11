package com.books.netty.server;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/27 10:10
 */
public class SimulationServerChannel {
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private SelectableChannel selectableChannel;
    private int readInterestOp;
    private List<MyHandler> myHandlerList;

    public SimulationServerChannel() {
        try {
            //打开服务通道
            selectableChannel = DEFAULT_SELECTOR_PROVIDER.openServerSocketChannel();
            //关注连接事件
            readInterestOp = SelectionKey.OP_ACCEPT;
            //设置非阻塞
            selectableChannel.configureBlocking(false);
            myHandlerList = new ArrayList<>(8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SelectableChannel getSelectableChannel() {
        return selectableChannel;
    }

    public int getReadInterestOp() {
        return readInterestOp;
    }

    public boolean isOpen() {
        return selectableChannel.isOpen();
    }


    public List<MyHandler> getMyHandlerList() {
        return myHandlerList;
    }

    public void addHandler(MyHandler myHandler){
        this.myHandlerList.add(myHandler);
    }
}
