package com.whxm.harbor.bean;

import java.math.BigDecimal;
import java.util.Date;

public class ShopVisit {

    private String shopNumber;

    private BigDecimal shopVisitAmount;

    private Date shopVisitTime;

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber == null ? null : shopNumber.trim();
    }

    public BigDecimal getShopVisitAmount() {
        return shopVisitAmount;
    }

    public void setShopVisitAmount(BigDecimal shopVisitAmount) {
        this.shopVisitAmount = shopVisitAmount;
    }

    public Date getShopVisitTime() {
        return shopVisitTime;
    }

    public void setShopVisitTime(Date shopVisitTime) {
        this.shopVisitTime = shopVisitTime;
    }
}