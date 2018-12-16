package com.din.mzitu.bean;

public class PostAllBean {

    private int type;
    private String url;
    private String image;
    private String title;


    public PostAllBean(int type, String url, String image, String title) {
        this.type = type;
        this.url = url;
        this.image = image;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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