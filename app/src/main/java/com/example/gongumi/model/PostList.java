package com.example.gongumi.model;

public class PostList {
    String postName;
    String term;

    public PostList() {

    };

    public PostList(String postName, String term) {
        this.postName = postName;
        this.term = term;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostName() {
        return postName;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }
}
