package com.whxm.harbor.model;

import com.whxm.harbor.bean.BizBuilding;

/**
 * @author : Ftibw
 * @date : 2018/12/20 10:04
 */
public class BuildingVo extends BizBuilding {

    private String shopNumber;
    private String terminalNumber;
    private String shopName;
    private String shopArea;

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopArea() {
        return shopArea;
    }

    public void setShopArea(String shopArea) {
        this.shopArea = shopArea;
    }
}
