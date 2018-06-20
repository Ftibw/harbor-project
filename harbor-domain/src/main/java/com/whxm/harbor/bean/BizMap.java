package com.whxm.harbor.bean;

public class BizMap {
    private Integer mapId;

    private String mapName;

    private String mapImgPath;

    private Integer floorId;

    //关联biz_floor表
    private String floorName="无";

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
}