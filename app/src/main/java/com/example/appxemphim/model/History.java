package com.example.appxemphim.model;

public class History {
    private int id;
    private int userId;
    private String watchedDate;
    private int secondsCount;
    private InformationMovie informationMovie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(String watchedDate) {
        this.watchedDate = watchedDate;
    }

    public int getSecondsCount() {
        return secondsCount;
    }

    public void setSecondsCount(int secondsCount) {
        this.secondsCount = secondsCount;
    }

    public InformationMovie getInformationMovie() {
        return informationMovie;
    }

    public void setInformationMovie(InformationMovie informationMovie) {
        this.informationMovie = informationMovie;
    }
}
