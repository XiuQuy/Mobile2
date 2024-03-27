package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class VideoYoutubeItem {

    @SerializedName("snippet")
    private VideoYoutubeSnippet snippet;

    public VideoYoutubeSnippet getSnippet() {
        return snippet;
    }

}
