package com.whxm.harbor.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BizScreensaverMaterial {
    private Integer screensaverMaterialId;

    private String screensaverMaterialImgPath;

    private Long screensaverMaterialSize;

    private String screensaverMaterialName;

    private String screensaverMaterialImgName;

    private String screensaverMaterialType;

    public Integer getScreensaverMaterialId() {
        return screensaverMaterialId;
    }

    public Integer screensaverId;

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