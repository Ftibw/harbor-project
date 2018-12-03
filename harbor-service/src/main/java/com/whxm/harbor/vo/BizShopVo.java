package com.whxm.harbor.vo;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopPicture;

import java.util.List;

public class BizShopVo extends BizShop {

    private List area;

    private Double dx;

    private Double dy;

    private Integer buildingType;

    private List<ShopPicture> pictures;

    public List<ShopPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ShopPicture> pictures) {
        this.pictures = pictures;
    }

    public List getArea() {
        return area;
    }

    public void setArea(List area) {
        this.area = area;
    }

    public Double getDx() {
        return dx;
    }

    public void setDx(Double dx) {
        this.dx = dx;
    }

    public Double getDy() {
        return dy;
    }

    public void setDy(Double dy) {
        this.dy = dy;
    }

    public Integer getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Integer buildingType) {
        this.buildingType = buildingType;
    }
}


