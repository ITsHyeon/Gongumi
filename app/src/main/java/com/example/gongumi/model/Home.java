package com.example.gongumi.model;

public class Home {
    int thumbnail;
    String product;
    String price;
    int progress;
    int people;

    public int getThumbnail() {
        return this.thumbnail;
    }

    public String getProduct() {
        return this.product;
    }

    public String getPrice() {
        return this.price;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getPeople() {
        return this.people;
    }

    public Home(int thumbnail, String product, String price, int progress, int people) {
        this.thumbnail = thumbnail;
        this.product = product;
        this.price = price;
        this.progress = progress;
        this.people = people;
    }

}
