package com.example.appxemphim.model;

import java.time.LocalDateTime;

public class History {
    private int id;
    private int userId;
    private LocalDateTime watchedDate;
    private int secondsCount;
    private InformationMovie informationMovie;

    public History(int id, int userId, LocalDateTime watchedDate, int secondsCount, InformationMovie informationMovie) {
        this.id = id;
        this.userId = userId;
        this.watchedDate = watchedDate;
        this.secondsCount = secondsCount;
        this.informationMovie = informationMovie;
    }

    public History() {
    }

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

    public LocalDateTime getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(LocalDateTime watchedDate) {
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
