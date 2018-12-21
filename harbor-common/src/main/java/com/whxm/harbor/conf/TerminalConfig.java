package com.whxm.harbor.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whxm.harbor.flag.MementoIF;
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

    //来个终端编号...
    @JsonIgnore
    public static final String cacheKey = "terminalCacheKey";

    /**
     * 创建备忘录返回标识接口
     */
    public MementoIF createMemento() {

        return new Memento(this);
    }

    /**
     * 将发起人恢复到备忘录对象所记录的状态上
     */
    public void restoreMemento(MementoIF memento) {

        Memento m = (Memento) memento;

        this.delay = m.getState().delay;

        this.onOff = m.getState().onOff;

        this.protect = m.getState().protect;
    }

    /**
     * 备忘录
     */
    private class Memento implements MementoIF {

        private String onOff;

        private String delay;

        private String protect;

        /**
         * 构造方法
         */
        private Memento(TerminalConfig state) {

            this.delay = state.delay;

            this.onOff = state.onOff;

            this.protect = state.protect;
        }

        private TerminalConfig getState() {

            TerminalConfig state = new TerminalConfig();

            state.setDelay(delay);

            state.setOnOff(onOff);

            state.setProtect(protect);

            return state;
        }

    }
}
