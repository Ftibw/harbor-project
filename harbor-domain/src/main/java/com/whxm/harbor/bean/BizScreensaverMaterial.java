package com.whxm.harbor.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BizScreensaverMaterial {
    private Integer screensaverMaterialId;

    private String screensaverMaterialImgPath;

    private Long screensaverMaterialSize;

    private String screensaverMaterialName;

    private String screensaverMaterialImgName;

    private String screensaverMaterialType;

    private Integer screensaverId;

    @JsonProperty("checked")
    private Integer hasBindThisScreensaver;

    public Integer getHasBindThisScreensaver() {
        return hasBindThisScreensaver;
    }

    public void setHasBindThisScreensaver(Integer hasBindThisScreensaver) {
        this.hasBindThisScreensaver = hasBindThisScreensaver;
    }


    public String getScreensaverMaterialImgName() {
        return screensaverMaterialImgName;
    }

    public void setScreensaverMaterialImgName(String screensaverMaterialImgName) {
        this.screensaverMaterialImgName = screensaverMaterialImgName;
    }

    @JsonIgnore
    public Integer getScreensaverId() {
        return screensaverId;
    }

    public void setScreensaverId(Integer screensaverId) {
        this.screensaverId = screensaverId;
    }

    public Integer getScreensaverMaterialId() {
        return screensaverMaterialId;
    }

    public void setScreensaverMaterialId(Integer screensaverMaterialId) {
        this.screensaverMaterialId = screensaverMaterialId;
    }

    public String getScreensaverMaterialImgPath() {
        return screensaverMaterialImgPath;
    }

    public void setScreensaverMaterialImgPath(String screensaverMaterialImgPath) {
        this.screensaverMaterialImgPath = screensaverMaterialImgPath == null ? null : screensaverMaterialImgPath.trim();
    }

    public Long getScreensaverMaterialSize() {
        return screensaverMaterialSize;
    }

    public void setScreensaverMaterialSize(Long screensaverMaterialSize) {
        this.screensaverMaterialSize = screensaverMaterialSize;
    }

    public String getScreensaverMaterialName() {
        return screensaverMaterialName;
    }

    public void setScreensaverMaterialName(String screensaverMaterialName) {
        this.screensaverMaterialName = screensaverMaterialName == null ? null : screensaverMaterialName.trim();
    }

    public String getScreensaverMaterialType() {
        return screensaverMaterialType;
    }

    public void setScreensaverMaterialType(String screensaverMaterialType) {
        this.screensaverMaterialType = screensaverMaterialType == null ? null : screensaverMaterialType.trim();
    }
}