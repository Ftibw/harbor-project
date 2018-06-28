package com.whxm.harbor.vo;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopPicture;

import java.util.List;

public class BizShopVo extends BizShop {

    private List<ShopPicture> pictures;

    public List<ShopPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ShopPicture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return "BizShopVo{" +
                "pictures=" + pictures +
                '}';
    }
}


