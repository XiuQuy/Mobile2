package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeVideoStatistics {
    @SerializedName("viewCount")
    private String viewCount;
    @SerializedName("likeCount")
    private String likeCount;
    @SerializedName("favoriteCount")
    private String favoriteCount;
    @SerializedName("commentCount")
    private String commentCount;


    public String getViewCount() {
        return viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public String getCommentCount() {
        return commentCount;
    }
}
