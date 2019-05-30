package com.example.gongumi.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String id; // ID
    private String pw; // 비밀번호
    private String name; // 이름
    private String location; // 위치

    // TODO : 채팅
    private String uid; // 누구와 채팅하고 있는지 상대방의 id를 받아옴
    private String profileUrl;

    public User() {}

    public User(String id, String pw, String uid, String name, String location, String profileUrl) {
        this.id = id;
        this.pw = pw;
        this.uid = uid;
        this.name = name;
        this.location = location;
        this.profileUrl = profileUrl;
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
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

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        result.put("uid", uid);
        result.put("name", name);
        result.put("location", location);
        result.put("profileUrl", profileUrl);

        return result;
    }

}
