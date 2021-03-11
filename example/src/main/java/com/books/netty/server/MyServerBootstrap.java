package com.books.netty.server;

import io.netty.channel.EventLoopGroup;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/27 10:22
 */
public class MyServerBootstrap {
    private volatile ThreadPoolExecutor childGroup;
    private volatile ThreadPoolExecutor bossGroup;
    private volatile MyHandler myHandler;
    private SocketAddress localAddress;
    private SimulationServerChannel simulationServerChannel;

    public MyServerBootstrap group(ThreadPoolExecutor bossGroup, ThreadPoolExecutor childGroup){
        this.bossGroup = bossGroup;
        this.childGroup = childGroup;
        return  this;
    }

    public MyServerBootstrap handler(MyHandler myHandler){
        this.myHandler = myHandler;
        return this;
    }

    public ThreadPoolExecutor getChildGroup() {
        return childGroup;
    }

    public void bind(){
        initAndRegister();
    }

    private void initAndRegister() {
        simulationServerChannel = new SimulationServerChannel();
        simulationServerChannel.addHandler(new AcceptorHandler(childGroup, myHandler));
    }

    public MyServerBootstrap localAddress(SocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    public ThreadPoolExecutor getBossGroup() {
        return bossGroup;
    }

    public MyHandler getMyHandler() {
        return myHandler;
    }
}
