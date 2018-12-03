package com.whxm.harbor.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.bean.ShopPicture;

import java.util.List;

/**
 * @author : Ftibw
 * @date : 2018/12/3 14:56
 */
public class BuildingVo extends BizBuilding {

    private List path;

    private String shopMessage;

    private List<ShopPicture> shopImg;

    public List getPath() {
        return path;
    }

    public void setPath(List path) {
        this.path = path;
    }

    public String getShopMessage() {
        return shopMessage;
    }

    public void setShopMessage(String shopMessage) {
        this.shopMessage = shopMessage;
    }

    public List<ShopPicture> getShopImg() {
        return shopImg;
    }

    public void setShopImg(List<ShopPicture> shopImg) {
        this.shopImg = shopImg;
    }
}
