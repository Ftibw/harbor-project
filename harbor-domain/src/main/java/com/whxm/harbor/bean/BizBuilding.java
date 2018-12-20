package com.whxm.harbor.bean;

import io.swagger.annotations.ApiModelProperty;

public class BizBuilding {

    @ApiModelProperty(hidden = true)
    private String id;
    @ApiModelProperty("建筑所在楼层ID")
    private Integer floorId;
    @ApiModelProperty("建筑类型")
    private Integer type;
    @ApiModelProperty("建筑x值")
    private Double dx;
    @ApiModelProperty("建筑y值")
    private Double dy;
    @ApiModelProperty("建筑区域")
    private String area;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}