package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ThumbnailVideoYoutube implements Serializable {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}
