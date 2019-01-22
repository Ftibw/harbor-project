package com.whxm.harbor.conf;

import com.whxm.harbor.ftp.FtpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class FtpConfig {

    @Value("${ftp.controlPort}")
    private int controlPort;
    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.user}")
    private String user;
    @Value("${ftp.password}")
    private String password;

    public FtpSession openSession(boolean isBinaryMode) {

        FtpSession utils = new FtpSession(10, 10, 10);

        try {
            utils.connect(host, controlPort, user, password, !isBinaryMode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return utils;
    }

    public void closeSession(FtpSession session) {
        try {
            session.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FtpSession utils = new FtpSession(10, 10, 10);

        try {
            utils.connect("121.40.178.13", 21, "iips", "YFiips5488", false);
            System.out.println(utils.getWorkingDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
