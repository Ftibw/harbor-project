package com.whxm.harbor.vo;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopPicture;
import com.whxm.harbor.utils.JacksonUtils;

import java.util.List;

public class BizShopVo extends BizShop {

    private String area;

    private String dx;

    private String dy;

    private List<ShopPicture> pictures;

    public List<ShopPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ShopPicture> pictures) {
        this.pictures = pictures;
    }

    public String getArea() {
        return area;
    }

    public void setArea(List area) {
        this.area = JacksonUtils.toJson(area);
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String dy) {
        this.dy = dy;
    }
}


