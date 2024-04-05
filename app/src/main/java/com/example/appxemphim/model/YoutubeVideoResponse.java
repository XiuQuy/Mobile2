package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeVideoResponse {
    @SerializedName("items")
    private YoutubeVideoItem[] items;

    public YoutubeVideoItem[] getItems() {
        return items;
    }
}
