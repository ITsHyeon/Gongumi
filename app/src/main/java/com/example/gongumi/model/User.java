package com.example.gongumi.model;

public class User {
    private String id; // ID
    private String pw; // 비밀번호
    private String name; // 이름
    private String location; // 위치

    public User() {}

    public User(String id, String pw, String name, String location) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPw() {
        return pw;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

}
