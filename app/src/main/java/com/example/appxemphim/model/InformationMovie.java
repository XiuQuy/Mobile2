package com.example.appxemphim.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InformationMovie {
    private int Id;
    private String movieId;
    private String title;
    private String tag;
    private String imageLink;
    private int durations;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getDurations() {
        return durations;
    }

    public void setDurations(int durations) {
        this.durations = durations;
    }

    public InformationMovie(int Id, String movieId, String title, String tag, String imageLink, int durations) {
        this.Id = Id;
        this.movieId = movieId;
        this.title = title;
        this.tag = tag;
        this.imageLink = imageLink;
        this.durations = durations;
    }
}

