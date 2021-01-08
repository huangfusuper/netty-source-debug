package com.books.netty_in_action.case_12.udp.entity;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * @author huangfu
 */
public class LogEvent implements Serializable {

    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress address;
    private final String logFile;
    private final String msg;
    private final long received;

    public LogEvent(String logFile, String msg) {
        this(null, logFile, msg,-1);
    }

    public LogEvent(InetSocketAddress address, String logFile, String msg, long received) {
        this.address = address;
        this.logFile = logFile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getLogFile() {
        return logFile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceived() {
        return received;
    }
}
