package com.example.demo.service;

import java.io.Serializable;

public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    private String goodName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}