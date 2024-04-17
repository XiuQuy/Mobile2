package com.example.appxemphim.model;

public class PlaylistItem {
    private int id;
    private int watchListId;
    private InformationMovie informationMovie;

    public PlaylistItem(int id, int watchListId, InformationMovie informationMovie) {
        this.id = id;
        this.watchListId = watchListId;
        this.informationMovie = informationMovie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWatchListId() {
        return watchListId;
    }

    public void setWatchListId(int watchListId) {
        this.watchListId = watchListId;
    }

    public InformationMovie getInformationMovie() {
        return informationMovie;
    }

    public void setInformationMovie(InformationMovie informationMovie) {
        this.informationMovie = informationMovie;
    }


}

