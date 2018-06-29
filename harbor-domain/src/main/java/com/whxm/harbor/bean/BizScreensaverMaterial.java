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
    //屏保ID screensaver_material_relation
    private Integer screensaverId;
    //终端ID terminal_first_page_relation
    private String terminalId;
    //是否为终端上的首页素材
    private Integer isFirstPage;
    //该素材是否绑定到了指定ID的屏保/终端(不可能同时绑定,屏保素材0/终端首页素材1)
    @JsonProperty("checked")
    private Integer hasBindThis;

    public Integer getIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(Integer isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }


    public Integer getHasBindThis() {
        return hasBindThis;
    }

    public void setHasBindThis(Integer hasBindThis) {
        this.hasBindThis = hasBindThis;
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