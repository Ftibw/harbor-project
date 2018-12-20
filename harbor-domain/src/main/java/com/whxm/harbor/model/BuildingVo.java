package com.whxm.harbor.model;

import com.whxm.harbor.bean.BizBuilding;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author : Ftibw
 * @date : 2018/12/20 10:04
 */
public class BuildingVo extends BizBuilding {
    @ApiModelProperty("商铺编号")
    private String shopNumber;
    @ApiModelProperty("终端编号")
    private String terminalNumber;
    @ApiModelProperty(hidden = true)
    private String shopName;
    @ApiModelProperty("商铺区域")
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
