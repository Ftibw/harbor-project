package com.whxm.harbor.bean;

import java.math.BigDecimal;
import java.util.Date;

public class ShopVisit {

    private String shopNumber;

    private BigDecimal shopVisitAmount;

    private Date shopVisitTime;

    //shopNumber连接biz_shop
    private String shopName;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

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