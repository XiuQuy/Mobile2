package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YoutubeVideoItem implements Serializable {

    @SerializedName("snippet")
    private YoutubeVideoSnippet snippet;
    @SerializedName("id")
    private String id;
    @SerializedName("statistics")
    private YoutubeVideoStatistics statistics;

    public YoutubeVideoSnippet getSnippet() {
        return snippet;
    }

    public String getId() {
        return id;
    }

    public YoutubeVideoStatistics getStatistics() {
        return statistics;
    }
}
