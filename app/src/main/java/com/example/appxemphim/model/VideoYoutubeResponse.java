package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class VideoYoutubeResponse {
    @SerializedName("items")
    private VideoYoutubeItem[] items;

    public VideoYoutubeItem[] getItems() {
        return items;
    }
}
