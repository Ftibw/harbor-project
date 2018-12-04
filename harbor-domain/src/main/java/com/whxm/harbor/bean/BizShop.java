package com.whxm.harbor.bean;


import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class BizShop {
    @ApiModelProperty(hidden = true)
    private String shopId;
    @ApiModelProperty(value = "商铺编号", required = true)
    private String shopNumber;
    @ApiModelProperty(value = "商铺名称", required = true)
    private String shopName;
    @ApiModelProperty(hidden = true)
    private String shopEnglishName;
    @ApiModelProperty(value = "商铺所在楼层ID", required = true)
    private Integer floorId;
    @ApiModelProperty(value = "商铺的业态ID", required = true)
    private Integer bizFormatId;
    @ApiModelProperty(hidden = true)
    private String shopHouseNumber;
    @ApiModelProperty(hidden = true)
    private Integer isShopEnabled;
    @ApiModelProperty(value = "商铺logo图片相对路径", required = true)
    private String shopLogoPath;
    @ApiModelProperty("商铺联系电话")
    private String shopTel;
    @ApiModelProperty(hidden = true)
    private Date addShopTime;
    @ApiModelProperty("商铺入住时间")
    private Date shopCheckinTime;
    @ApiModelProperty(value = "商铺权重(用于排序)", required = true)
    private Integer shopWeight;
    @ApiModelProperty("商铺描述")
    private String shopDescript;
    @ApiModelProperty(hidden = true)
    //join biz_floor
    private String floorName;
    @ApiModelProperty(hidden = true)
    //join biz_format
    private String bizFormatType;

    public String getBizFormatType() {
        return bizFormatType;
    }

    public void setBizFormatType(String bizFormatType) {
        this.bizFormatType = bizFormatType;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId == null ? null : shopId.trim();
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber == null ? null : shopNumber.trim();
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public String getShopEnglishName() {
        return shopEnglishName;
    }

    public void setShopEnglishName(String shopEnglishName) {
        this.shopEnglishName = shopEnglishName == null ? null : shopEnglishName.trim();
    }

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public Integer getBizFormatId() {
        return bizFormatId;
    }

    public void setBizFormatId(Integer bizFormatId) {
        this.bizFormatId = bizFormatId;
    }

    public String getShopHouseNumber() {
        return shopHouseNumber;
    }

    public void setShopHouseNumber(String shopHouseNumber) {
        this.shopHouseNumber = shopHouseNumber == null ? null : shopHouseNumber.trim();
    }

    public Integer getIsShopEnabled() {
        return isShopEnabled;
    }

    public void setIsShopEnabled(Integer isShopEnabled) {
        this.isShopEnabled = isShopEnabled;
    }

    public String getShopLogoPath() {
        return shopLogoPath;
    }

    public void setShopLogoPath(String shopLogoPath) {
        this.shopLogoPath = shopLogoPath == null ? null : shopLogoPath.trim();
    }

    public String getShopTel() {
        return shopTel;
    }

    public void setShopTel(String shopTel) {
        this.shopTel = shopTel == null ? null : shopTel.trim();
    }

    public Date getAddShopTime() {
        return addShopTime;
    }

    public void setAddShopTime(Date addShopTime) {
        this.addShopTime = addShopTime;
    }

    public Date getShopCheckinTime() {
        return shopCheckinTime;
    }

    public void setShopCheckinTime(Date shopCheckinTime) {
        this.shopCheckinTime = shopCheckinTime;
    }

    public Integer getShopWeight() {
        return shopWeight;
    }

    public void setShopWeight(Integer shopWeight) {
        this.shopWeight = shopWeight;
    }

    public String getShopDescript() {
        return shopDescript;
    }

    public void setShopDescript(String shopDescript) {
        this.shopDescript = shopDescript == null ? null : shopDescript.trim();
    }
}