package com.whxm.harbor.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("导航路点的边关系对象(如:A->B,A为尾点,B为头点)")
public class MapEdge {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @ApiModelProperty(value = "有向边的尾点", required = true)
    private Integer tail;
    @ApiModelProperty(value = "有向边的头点", required = true)
    private Integer head;
    @ApiModelProperty(value = "头尾点之间距离", required = true)
    private Double distance;
    @ApiModelProperty(value = "尾点走到头点的耗时")
    private Double time;
    @ApiModelProperty(value = "边是单向还是双向(0表示双向,1表示单向)", required = true)
    private Integer isDirected;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTail() {
        return tail;
    }

    public void setTail(Integer tail) {
        this.tail = tail;
    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Integer getIsDirected() {
        return isDirected;
    }

    public void setIsDirected(Integer isDirected) {
        this.isDirected = isDirected;
    }
}