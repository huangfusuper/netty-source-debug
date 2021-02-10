package com.books.cases;

import java.io.Serializable;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/10 13:20
 */
public class TestMark implements Serializable {
    private static final long serialVersionUID = -2169931246989671127L;
    final static int init = 1;
    final static int exe = 1 << 1;
    final static int read = 1 << 2;
    final static int write = 1 << 3;

    //可读可写
    final static int writeRead = read | write;
    //可执行可写
    final static int exeRead = exe | read;


    public static void main(String[] args) {
        //判断是不是可执行的
        System.out.println((exe & exeRead) == exe);
        //判断是不是可执行的
        System.out.println((exe & writeRead) == exe);
        //是不是可读可写的
        System.out.println(((read | write) & writeRead) == (read | write));
        //构建一个具有全部权限的
        //int all = writeRead | exeRead;
        //这样也行
        int all1 = exe | read | write;
        System.out.println((write & all1) == write);
        //把all权限剔除到只剩余一个读权限的任务
        all1 &= ~exe;
        all1 &= ~write;
        //是否包含写权限
        System.out.println((write & all1) == write);
        //是否包含读权限（包含）
        System.out.println((read & all1) == read);
    }
}
