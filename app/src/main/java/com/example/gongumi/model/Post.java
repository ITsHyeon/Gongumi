package com.example.gongumi.model;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private User user; // 유저 정보
    private String category; // 카테고리
    private Date startDay, endDay; // 공구 시작 날짜, 끝나는 날짜
    private int num; // 공구 인원
    private int people = 0; // 공구 참여한 사람 인원
    private String product; // 상품명
    private int price; // 가격
    private String content; // 설명
    private String url; // 상품 url

    public Post() {}
    public Post(User user, String category, Date startDay, Date endDay, int num, int people, String product, int price, String content, String url) {
        this.user = user;
        this.category = category;
        this.startDay = startDay;
        this.endDay = endDay;
        this.num = num;
        this.people = people;
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

    public void setPeople(int people) {
        this.people = people;
    }

    public int getPeople() {
        return people;
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
