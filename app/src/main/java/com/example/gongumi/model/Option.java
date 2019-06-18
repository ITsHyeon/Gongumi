package com.example.gongumi.model;

public class Option {
    private String url;
    private String name;
    private String opt;
    private String qty;

    public Option() {}

    public Option(String url, String name, String opt, String qty) {
        this.url = url;
        this.name = name;
        this.opt = opt;
        this.qty = qty;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getOpt() {
        return opt;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getQty() {
        return qty;
    }
}
