package com.example.appxemphim.model;

public class InformationMovie {
    private int id;
    private String movieId;
    private String title;
    private String tag;
    private int durations;
    private String imageLink;

    public InformationMovie(int id, String movieId, String title, String tag, String imageLink, int durations) {
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.tag = tag;
        this.imageLink = imageLink;
        this.durations = durations;
    }

    public InformationMovie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
