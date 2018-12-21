package com.whxm.harbor.bean;

public class BizBuilding {
    private Integer id;

    private String number;

    private String name;

    private String color;

    private Integer layer;

    private Integer type;

    private String width;

    private String height;

    private String pageX;

    private String pageY;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width == null ? null : width.trim();
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height == null ? null : height.trim();
    }

    public String getPageX() {
        return pageX;
    }

    public void setPageX(String pageX) {
        this.pageX = pageX == null ? null : pageX.trim();
    }

    public String getPageY() {
        return pageY;
    }

    public void setPageY(String pageY) {
        this.pageY = pageY == null ? null : pageY.trim();
    }
}