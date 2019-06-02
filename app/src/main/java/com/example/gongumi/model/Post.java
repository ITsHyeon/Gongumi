package com.example.gongumi.model;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private String userId; // 공구한 사람 id
    private String userUid; // 공구한 사람 uid
    private String hashtag; // 카테고리
    private Date startDay, endDay; // 공구 시작 날짜, 끝나는 날짜
    private int num; // 공구 인원
    private int people = 0; // 공구 참여한 사람 인원
    private String product; // 상품명
    private int price; // 가격
    private String content; // 설명
    private String url; // 상품 url
    private int imgCount; // 이미지 개수
    private String location; // 주소

    public Post() {}
    public Post(String userId, String userUid, String hashtag, Date startDay, Date endDay, int num, int people, String product, int price, String content, String url, int imgCount) {
        this.userId = userId;
        this.userUid = userUid;
        this.hashtag = hashtag;
        this.startDay = startDay;
        this.endDay = endDay;
        this.num = num;
        this.people = people;
        this.product = product;
        this.price = price;
        this.content = content;
        this.url = url;
        this.imgCount = imgCount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getHashtag() {
        return hashtag;
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

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }


}
