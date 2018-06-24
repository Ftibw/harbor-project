package com.whxm.harbor.conf;

import com.whxm.harbor.utils.FtpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class FtpConfig {

    @Value("${ftp.controlPort}")
    private int controlPort;
    @Value("${ftp.host}")
    private String host;
    @Value("ftp.user")
    private String user;
    @Value("${ftp.password}")
    private String password;

    @Bean
    public FtpUtils getFtpUtils() throws IOException {

        FtpUtils utils = new FtpUtils(10, 10, 10);

        utils.connect(host, controlPort, user, password, false);

        return utils;
    }


}
