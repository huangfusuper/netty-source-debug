package com.books.nio.channel.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * *********************************************************************
 * 文件复制
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/1/1 15:17
 */
public class FileCopy implements Serializable {
    private static final long serialVersionUID = 1589846536326961155L;

    public static void main(String[] args) {
        File file = new File("C:/Users/huangfu/Desktop/123/123.docx");
        File fileCopy = new File("C:/Users/huangfu/Desktop/123/123_copy.docx");

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inChannel = null;
        FileChannel outputStreamChannel = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(fileCopy);

            inChannel = inputStream.getChannel();
            outputStreamChannel = outputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(8);

            while ((inChannel.read(byteBuffer)) != -1) {
                byteBuffer.flip();
                outputStreamChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            //强制刷新到磁盘
            outputStreamChannel.force(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (inChannel != null) {
                    inChannel.close();
                }

                if (outputStreamChannel != null) {
                    outputStreamChannel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
