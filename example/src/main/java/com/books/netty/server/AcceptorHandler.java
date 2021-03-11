package com.books.netty.server;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/27 10:35
 */
public class AcceptorHandler implements MyHandler {
    private ThreadPoolExecutor childGroup;
    private MyHandler myHandler;

    public AcceptorHandler(ThreadPoolExecutor childGroup, MyHandler myHandler) {
        this.childGroup = childGroup;
        this.myHandler = myHandler;
    }
}
