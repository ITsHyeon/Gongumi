package com.example.gongumi.model;

import java.util.Date;

public class Home {
    int thumbnail;
    String product;
    String price;
    String url;
    int progress;
    int people;
    String content;
    Date startDay;
    String time;

    public void setThumbnail(int thumbnail) { this.thumbnail = thumbnail; }

    public int getThumbnail() {
        return this.thumbnail;
    }

    public String getProduct() {
        return this.product;
    }

    public String getPrice() {
        return this.price;
    }

    public String getUrl() {
        return this.url;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getPeople() { return this.people; }

    public String getContent() { return content; }

    public String getTime() { return time; }

    public Home(String product, String price, String url, int progress, int people, String content, Date startDay) {
        this.product = product;
        this.price = price;
        this.url = url;
        this.progress = progress;
        this.people = people;
        this.content = content;
        this.startDay = startDay;
        this.time = String.valueOf(startDay.getTime());
    }

}
