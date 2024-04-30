package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeChannelResponse {
    @SerializedName("items")
    private List<YoutubeChannelItem> items;

    public List<YoutubeChannelItem> getItems() {
        return items;
    }
}

