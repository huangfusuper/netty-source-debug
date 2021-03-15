package com.books.nio.channel.file;

import java.io.*;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/3/13 19:03
 */
public class TransferTo {

    public static void main(String[] args) throws IOException {


        RandomAccessFile rw = new RandomAccessFile("C:\\Users\\huangfu\\Desktop/640.gif", "rw");
        RandomAccessFile rw_copy = new RandomAccessFile("C:\\Users\\huangfu\\Desktop/640_copy.gif", "rw");

        rw.getChannel().transferTo(0,rw.length(),rw_copy.getChannel());

        rw.close();
        rw_copy.close();
    }
}
