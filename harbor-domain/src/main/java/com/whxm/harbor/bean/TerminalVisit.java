package com.whxm.harbor.bean;

import java.math.BigDecimal;
import java.util.Date;

public class TerminalVisit {
    private String terminalNumber;

    private BigDecimal terminalVisitAmount;

    private Date terminalVisitTime;

    public String getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber == null ? null : terminalNumber.trim();
    }

    public BigDecimal getTerminalVisitAmount() {
        return terminalVisitAmount;
    }

    public void setTerminalVisitAmount(BigDecimal terminalVisitAmount) {
        this.terminalVisitAmount = terminalVisitAmount;
    }

    public Date getTerminalVisitTime() {
        return terminalVisitTime;
    }

    public void setTerminalVisitTime(Date terminalVisitTime) {
        this.terminalVisitTime = terminalVisitTime;
    }
}