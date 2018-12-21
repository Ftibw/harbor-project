package com.whxm.harbor.vo;

import com.whxm.harbor.bean.BizBuilding;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author : Ftibw
 * @date : 2018/12/20 10:04
 */
public class BuildingVo extends BizBuilding {
    @ApiModelProperty("商铺ID")
    private String sid;
    @ApiModelProperty("终端ID")
    private String tid;
    @ApiModelProperty(hidden = true)
    private String shopName;
    @ApiModelProperty(hidden = true)
    private String shopNumber;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

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
        this.shopNumber = shopNumber;
    }
}
