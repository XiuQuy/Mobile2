package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeVideoSnippet {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("channelId")
    private String channelId;

    @SerializedName("channelTitle")
    private String channelTitle;

    @SerializedName("thumbnails")
    private ThumbnailsVideoYoutube thumbnails;

    @SerializedName("publishedAt")
    private String publishedAt;

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getChannelId() {
        return channelId;
    }

    public ThumbnailsVideoYoutube getThumbnails() {
        return thumbnails;
    }
}
