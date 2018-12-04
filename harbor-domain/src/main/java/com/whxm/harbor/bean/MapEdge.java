package com.whxm.harbor.bean;

public class MapEdge {
    private Integer id;

    private Integer tail;

    private Integer head;

    private Double distance;

    private Double time;

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