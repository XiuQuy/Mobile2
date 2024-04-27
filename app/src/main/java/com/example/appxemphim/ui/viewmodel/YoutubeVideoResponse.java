package com.example.appxemphim.ui.viewmodel;

import com.example.appxemphim.model.YoutubeVideoItem;
import com.google.gson.annotations.SerializedName;

public class YoutubeVideoResponse {
    @SerializedName("items")
    private YoutubeVideoItem[] items;

    public YoutubeVideoItem[] getItems() {
        return items;
    }
}
