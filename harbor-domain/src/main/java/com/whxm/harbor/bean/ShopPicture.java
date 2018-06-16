package com.whxm.harbor.bean;

public class ShopPicture {

    private String shopId;

    private String shopPicturePath;

    private String shopPictureName;

    private Long shopPictureSize;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopPicturePath() {
        return shopPicturePath;
    }

    public void setShopPicturePath(String shopPicturePath) {
        this.shopPicturePath = shopPicturePath;
    }

    public String getShopPictureName() {
        return shopPictureName;
    }

    public void setShopPictureName(String shopPictureName) {
        this.shopPictureName = shopPictureName;
    }

    public Long getShopPictureSize() {
        return shopPictureSize;
    }

    public void setShopPictureSize(Long shopPictureSize) {
        this.shopPictureSize = shopPictureSize;
    }
}

