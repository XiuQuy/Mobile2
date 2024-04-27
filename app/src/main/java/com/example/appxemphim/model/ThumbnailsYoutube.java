package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ThumbnailsYoutube implements Serializable {
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
