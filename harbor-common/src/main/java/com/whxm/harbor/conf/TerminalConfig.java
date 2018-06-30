package com.whxm.harbor.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
public class TerminalConfig implements Serializable {

    @JsonProperty("on_off")
    @Value("${terminal.on-off}")
    private String onOff;

    @Value("${terminal.delay}")
    private String delay;

    @Value("${terminal.protect}")
    private String protect;

    public String getOnOff() {
        return onOff;
    }

    public void setOnOff(String onOff) {
        this.onOff = onOff;
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

    @JsonIgnore
    public static final String cacheKey = "terminalCacheKey";
}
