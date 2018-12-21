package com.whxm.harbor.model;

import java.math.BigDecimal;

public class TerminalVisitModel {

    private String terminalNumber;

    private String terminalName;

    private String terminalType;

    private String terminalIp;

    private String floorName;

    private Integer isTerminalOnline;

    private BigDecimal terminalVisitAmount;

    public String getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getIsTerminalOnline() {
        return isTerminalOnline;
    }

    public void setIsTerminalOnline(Integer isTerminalOnline) {
        this.isTerminalOnline = isTerminalOnline;
    }

    public BigDecimal getTerminalVisitAmount() {
        return terminalVisitAmount;
    }

    public void setTerminalVisitAmount(BigDecimal terminalVisitAmount) {
        this.terminalVisitAmount = terminalVisitAmount;
    }
}
