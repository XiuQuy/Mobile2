package com.example.appxemphim.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class YoutubeVideoItem implements Serializable {

    @SerializedName("snippet")
    private YoutubeVideoSnippet snippet;
    @SerializedName("id")
    private String id;
    @SerializedName("statistics")
    private YoutubeVideoStatistics statistics;

    @SerializedName("contentDetails")
    private YoutubeVideoContentDetails contentDetails;

    public YoutubeVideoSnippet getSnippet() {
        return snippet;
    }

    public String getId() {
        return id;
    }

    public YoutubeVideoStatistics getStatistics() {
        return statistics;
    }

    public YoutubeVideoContentDetails getContentDetails() {
        return contentDetails;
    }
}

