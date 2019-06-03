package com.example.gongumi.model;

import java.io.Serializable;

public class ChatList implements Serializable {
    private String thumbnailUrl;
    private Post post;
    private Chat chat;

    public ChatList() {};

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

}
