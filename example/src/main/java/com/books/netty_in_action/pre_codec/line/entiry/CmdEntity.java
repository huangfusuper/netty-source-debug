package com.books.netty_in_action.pre_codec.line.entiry;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;

/**
 * *********************************************************************
 * 命令实体
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/12/27 14:10
 */
public class CmdEntity implements Serializable {
    private static final long serialVersionUID = 4287228428224959102L;

    private String cmd;
    private String args;

    public CmdEntity(String cmd, String args) {
        this.cmd = cmd;
        this.args = args;
    }

    public CmdEntity() {
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
