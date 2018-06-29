package com.whxm.harbor.conf;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TerminalConfig {

    private String on_off = "00:00-24:00";
    private String delay = "10";
    private String protect = "300";

    public String getOn_off() {
        return on_off;
    }

    public void setOn_off(String on_off) {
        this.on_off = on_off;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getProtect() {
        return protect;
    }

    public void setProtect(String protect) {
        this.protect = protect;
    }
}
