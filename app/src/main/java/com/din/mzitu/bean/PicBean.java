package com.din.mzitu.bean;

public class PicBean {

    private int type;
    private String image;
    private String title;

    public PicBean(int type, String image, String title) {
        this.type = type;
        this.image = image;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
} 