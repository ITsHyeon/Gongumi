package com.example.gongumi.model;

public class JoinList {
    String postName;
    String term;
    String option;
    String quantity;

    public JoinList() {

    };

    public JoinList(String postName, String term, String option, String quantity) {
        this.postName = postName;
        this.term = term;
        this.option = option;
        this.quantity = quantity;
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

    public void setOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }
}
