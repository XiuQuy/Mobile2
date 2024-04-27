package com.example.appxemphim.ui.viewmodel;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }
}