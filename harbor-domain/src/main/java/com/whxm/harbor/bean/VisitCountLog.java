package com.whxm.harbor.bean;

import java.util.Date;

public class VisitCountLog {
    private String terminalId;

    private String terminalIp;

    private String shopId;

    private Date createCountTime;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp == null ? null : terminalIp.trim();
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId == null ? null : shopId.trim();
    }

    public Date getCreateCountTime() {
        return createCountTime;
    }

    public void setCreateCountTime(Date createCountTime) {
        this.createCountTime = createCountTime;
    }
}