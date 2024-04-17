package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeVideoItem {

    @SerializedName("snippet")
    private YoutubeVideoSnippet snippet;

    public YoutubeVideoSnippet getSnippet() {
        return snippet;
    }

}
