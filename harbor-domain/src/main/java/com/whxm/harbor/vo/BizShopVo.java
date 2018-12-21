package com.whxm.harbor.vo;

import com.whxm.harbor.bean.BizShop;
import com.whxm.harbor.bean.ShopPicture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "商铺建筑参数")
public class BizShopVo extends BizShop {
    @ApiModelProperty(value = "商铺建筑的区域点集合")
    private List<AreaPoint> area;
    @ApiModelProperty(value = "商铺建筑的中心点x值", required = true)
    private Double dx;
    @ApiModelProperty(value = "商铺建筑的中心点y值", required = true)
    private Double dy;
    @ApiModelProperty(value = "商铺建筑的类型", required = true)
    private Integer buildingType;
    @ApiModelProperty(value = "商铺的图片列表", required = true)
    private List<ShopPicture> pictures;

    public List<ShopPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ShopPicture> pictures) {
        this.pictures = pictures;
    }

    public List<AreaPoint> getArea() {
        return area;
    }

    public void setArea(List<AreaPoint> area) {
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

@ApiModel(value = "商铺建筑区域点")
class AreaPoint {
    @ApiModelProperty(value = "区域点的x值", required = true)
    private Double _dx;
    @ApiModelProperty(value = "区域点的y值", required = true)
    private Double _dy;

    public Double get_dx() {
        return _dx;
    }

    public void set_dx(Double _dx) {
        this._dx = _dx;
    }

    public Double get_dy() {
        return _dy;
    }

    public void set_dy(Double _dy) {
        this._dy = _dy;
    }
}

