package com.whxm.harbor.bean;

import java.util.Date;

public class ShopVisit {
    private Integer id;

    private String shopId;

    private Long shopVisitAmount;

    private Date shopVisitTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId == null ? null : shopId.trim();
    }

    public Long getShopVisitAmount() {
        return shopVisitAmount;
    }

    public void setShopVisitAmount(Long shopVisitAmount) {
        this.shopVisitAmount = shopVisitAmount;
    }

    public Date getShopVisitTime() {
        return shopVisitTime;
    }

    public void setShopVisitTime(Date shopVisitTime) {
        this.shopVisitTime = shopVisitTime;
    }
}