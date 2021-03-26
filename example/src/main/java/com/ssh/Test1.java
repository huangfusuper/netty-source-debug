package com.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author huangfu
 * @date
 */
public class Test1 {
    public static void main(String[] args) throws JSchException, IOException {
        JSch jSch  = new JSch();
        Session session = jSch.getSession("root", "10.0.55.79", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword("123456");
        session.connect();
        System.out.println(execCommandByJSch(session, "ls /", "utf-8"));

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

}
