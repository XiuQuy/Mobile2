package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class ThumbnailsVideoYoutube {
    @SerializedName("default")
    private ThumbnailVideoYoutube defaultThumbnail;

    @SerializedName("medium")
    private ThumbnailVideoYoutube mediumThumbnail;

    public ThumbnailVideoYoutube getDefaultThumbnail() {
        return defaultThumbnail;
    }

    public ThumbnailVideoYoutube getMediumThumbnail() {
        return mediumThumbnail;
    }
}
