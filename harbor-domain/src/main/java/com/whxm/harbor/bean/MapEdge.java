package com.whxm.harbor.bean;

public class MapEdge extends MapEdgeKey {

    private Double distance;

    private Double time;

    private Integer isDirected;

    private Integer floorId;

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

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }
}