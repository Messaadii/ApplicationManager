package com.vermeg.ApplicationManager;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class Test {

    public static void main(String[] args) throws JSchException, IOException, SftpException {
        /*JSch jsch = new JSch();
        Session session = jsch.getSession("tayyouta", "192.168.56.101", 22);
        session.setPassword("tey80");

        // Avoid asking for key confirmation
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");

        sftpChannel.connect();

        // Transfer the file
        try (InputStream inputStream = new ByteArrayInputStream("hi".getBytes())) {
            sftpChannel.put(inputStream, "/home/tayyouta/Desktop/test.jpg");
        }

        sftpChannel.exit();
        session.disconnect();*/

    }

}
