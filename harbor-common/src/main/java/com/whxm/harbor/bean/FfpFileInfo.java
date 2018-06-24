package com.whxm.harbor.bean;

import java.sql.Timestamp;
import java.util.Calendar;

public class FfpFileInfo {

    private String name;
    private Long size;
    private Timestamp timestamp;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = new Timestamp(timestamp.getTime().getTime());
    }

    public String getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type ? "目录" : "文件";
    }
}
