package com.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author huangfu
 * @date
 */
public class Test1 {
    public static void main(String[] args) throws Exception {

        JSch jSch = new JSch();
        Session session = jSch.getSession("root", "10.0.55.79", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword("123456");
        session.connect();
        try{
        listFolderStructure(session, "ls");
        listFolderStructure(session, "ls");
        listFolderStructure(session, "ls");

        }finally {
            session.disconnect();

        }
    }

    public static String execCommandByJSch(Session session, String command, String resultEncoding) throws IOException, JSchException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();

        String result = IOUtils.toString(in, resultEncoding);
        channelExec.disconnect();

        return result;
    }


    public static void listFolderStructure(Session session, String command) throws Exception {


        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = responseStream.toString();
            System.out.println(responseString);
        }finally {
            if(channel != null) {
                channel.disconnect();
            }
        }
    }

}
