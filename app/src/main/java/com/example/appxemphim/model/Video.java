package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Video implements Serializable {
    @SerializedName("key")
    private String key;

    public String getKey() {
        return key;
    }
}
