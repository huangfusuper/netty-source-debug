package com.books.netty_in_action.chapter_two.jdk.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * BIO 服务器想客户端打印一句话
 *
 * @author huangfu
 * @date 2020年12月18日10:44:28
 */
public class BioEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8989);
        try {
            for (;;){
                //获取一个客户端的连接
                Socket accept = serverSocket.accept();
                System.out.println(String.format("检测到有客户端接入：%s", accept));
                //获取客户端的输出流
                OutputStream outputStream = accept.getOutputStream();
                outputStream.write("Hi Bio".getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                //关闭通道
                accept.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }
}
