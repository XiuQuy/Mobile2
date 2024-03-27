package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class ThumbnailsVideoYoutube {
    @SerializedName("default")
    private ThumbnailVideoYoutube defaultThumbnail;

    public ThumbnailVideoYoutube getDefaultThumbnail() {
        return defaultThumbnail;
    }
}
