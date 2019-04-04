package com.example.gongumi.model;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private User user;
    private String category;
    private Date startDay, endDay;
    private int num;
    private String product;
    private int price;
    private String content;
    private String url;

    public Post() {}
    public Post(User user, String category, Date startDay, Date endDay, int num, String product, int price, String content, String url) {
        this.user = user;
        this.category = category;
        this.startDay = startDay;
        this.endDay = endDay;
        this.num = num;
        this.product = product;
        this.price = price;
        this.content = content;
        this.url = url;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
