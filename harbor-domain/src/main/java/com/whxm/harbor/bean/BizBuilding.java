package com.whxm.harbor.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("建筑对象")
public class BizBuilding {

    public static final Integer TYPE_LINE = 0;//路线点
    public static final Integer TYPE_TOILET = 1;//厕所
    public static final Integer TYPE_ELEVATOR = 2;//电梯
    public static final Integer TYPE_STAIR = 3;//楼梯
    public static final Integer TYPE_TERMINAL = 4;//终端
    public static final Integer TYPE_SHOP = 5;//商铺

    @ApiModelProperty(hidden = true)
    private Integer id;
    @ApiModelProperty("建筑编号")
    private String number;
    @ApiModelProperty("建筑名称")
    private String name;
    @ApiModelProperty(value = "建筑所在楼层", required = true)
    private Integer layer;
    @ApiModelProperty(value = "特指建筑的类型", required = true)
    private Integer type;
    @ApiModelProperty("建筑的区域点集合")
    private String area;
    @ApiModelProperty(value = "建筑的x值", required = true)
    private Double dx;
    @ApiModelProperty(value = "建筑的y值", required = true)
    private Double dy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
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
}