package com.example.android.mediarecorder;

public class Product {

    public String video_path;
    public Integer price;
    public Product(String name, Integer price) {
        super();
        this.video_path = name;
        this.price = price;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
}