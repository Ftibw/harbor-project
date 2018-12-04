package com.whxm.harbor.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "商铺图片参数")
public class ShopPicture {
    @ApiModelProperty(hidden = true)
    private String shopId;
    @ApiModelProperty(value = "商铺图片相对路径", required = true)
    private String shopPicturePath;
    @ApiModelProperty(value = "商铺图片(服务器生成的)新名字", required = true)
    private String shopPictureName;
    @ApiModelProperty(value = "商铺图片大小", required = true)
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

