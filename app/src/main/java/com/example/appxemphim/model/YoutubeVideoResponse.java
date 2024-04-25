package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeVideoResponse {
    @SerializedName("items")
    private List<YoutubeVideoItem> items;

    public List<YoutubeVideoItem> getItems() {
        return items;
    }
}
