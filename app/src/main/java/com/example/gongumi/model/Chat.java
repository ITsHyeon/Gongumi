package com.example.gongumi.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Chat implements Serializable {

    public Map<String, Boolean> users = new HashMap<>(); // 채팅방의 유저들
    public Map<String, Comment> comments = new HashMap<>(); // 채팅방의 대화내용
    public long lastReadTime;

    public static class Comment{
       public String uid;
       public String message;
       // public Object timestamp;
       public long timestamp;
    }
}
