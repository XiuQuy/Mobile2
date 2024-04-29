package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeChannelSnippet {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("thumbnails")
    private ThumbnailsYoutube thumbnails;

    @SerializedName("publishedAt")
    private String publishedAt;

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ThumbnailsYoutube getThumbnails() {
        return thumbnails;
    }
}

