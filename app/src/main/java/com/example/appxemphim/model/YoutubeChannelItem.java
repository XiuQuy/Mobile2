package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YoutubeChannelItem implements Serializable {

    @SerializedName("snippet")
    private YoutubeChannelSnippet snippet;
    @SerializedName("id")
    private String id;
    public YoutubeChannelSnippet getSnippet() {
        return snippet;
    }

    public String getId() {
        return id;
    }

}
