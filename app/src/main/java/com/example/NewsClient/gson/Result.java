package com.example.NewsClient.gson;

import java.util.List;

public class Result {

    private String stat;

    List<Data> data;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
