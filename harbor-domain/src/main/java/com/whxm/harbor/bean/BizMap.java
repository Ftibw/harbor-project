package com.whxm.harbor.bean;

public class BizMap {
    private Integer mapId;
    private String mapName;
    private String mapImgPath;
    private Integer floorId;
    private Integer height;
    private Integer width;
    private Double originX;
    private Double originY;
    private Double scale;
    private Double rotate;

    //关联biz_floor表
    private String floorName = "无";

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName == null ? null : mapName.trim();
    }

    public String getMapImgPath() {
        return mapImgPath;
    }

    public void setMapImgPath(String mapImgPath) {
        this.mapImgPath = mapImgPath == null ? null : mapImgPath.trim();
    }

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    //extends
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Double getOriginX() {
        return originX;
    }

    public void setOriginX(Double originX) {
        this.originX = originX;
    }

    public Double getOriginY() {
        return originY;
    }

    public void setOriginY(Double originY) {
        this.originY = originY;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Double getRotate() {
        return rotate;
    }

    public void setRotate(Double rotate) {
        this.rotate = rotate;
    }
}