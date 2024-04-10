package com.example.appxemphim.model;

import java.util.Date;

public class History {
    private int Id;
    private int userId;
    private String watchedDate;
    private int secondsCount;
    private InformationMovie informationMovie;

    public int getId() {return Id;}

    public void setId(int id) {Id = id;}

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

    // Constructors, toString, hashCode, equals methods can be added as needed

    public History(int Id, int userId, String watchedDate, int secondsCount, InformationMovie informationMovie) {
        this.Id = Id;
        this.userId = userId;
        this.watchedDate = watchedDate;
        this.secondsCount = secondsCount;
        this.informationMovie = informationMovie;
    }
}
