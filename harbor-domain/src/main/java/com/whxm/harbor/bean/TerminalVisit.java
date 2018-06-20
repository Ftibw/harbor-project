package com.whxm.harbor.bean;

import java.util.Date;

public class TerminalVisit {
    private Integer id;

    private String terminalId;

    private Long terminalVisitAmount;

    private Date terminalVisitTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    public Long getTerminalVisitAmount() {
        return terminalVisitAmount;
    }

    public void setTerminalVisitAmount(Long terminalVisitAmount) {
        this.terminalVisitAmount = terminalVisitAmount;
    }

    public Date getTerminalVisitTime() {
        return terminalVisitTime;
    }

    public void setTerminalVisitTime(Date terminalVisitTime) {
        this.terminalVisitTime = terminalVisitTime;
    }
}